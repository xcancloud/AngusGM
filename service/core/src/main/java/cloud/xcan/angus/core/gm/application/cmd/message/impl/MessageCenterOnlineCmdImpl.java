package cloud.xcan.angus.core.gm.application.cmd.message.impl;

import static cloud.xcan.angus.core.gm.application.converter.MessageCenterConverter.assembleMessageCenterOnline;
import static cloud.xcan.angus.core.gm.infra.message.MessageConnectionListener.LOCAL_ONLINE_USERS;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;

import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.commonlink.user.UserRepo;
import cloud.xcan.angus.api.enums.ReceiveObjectType;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.message.MessageCenterOnlineCmd;
import cloud.xcan.angus.core.gm.domain.message.center.MessageCenterOnline;
import cloud.xcan.angus.core.gm.domain.message.center.MessageCenterOnlineRepo;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.security.authentication.service.JdbcOAuth2AuthorizationService;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of message center online command operations for managing user online status.
 * 
 * <p>This class provides comprehensive functionality for online status management including:</p>
 * <ul>
 *   <li>Managing user online/offline status</li>
 *   <li>Handling session-based status updates</li>
 *   <li>Forcing user logout and token invalidation</li>
 *   <li>Managing application shutdown procedures</li>
 * </ul>
 * 
 * <p>The implementation ensures proper user session management with
 * OAuth2 token invalidation and status tracking.</p>
 */
@Slf4j
@Biz
public class MessageCenterOnlineCmdImpl extends CommCmd<MessageCenterOnline, Long>
    implements MessageCenterOnlineCmd {

  @Resource
  private UserRepo userRepo;
  @Resource
  private MessageCenterOnlineRepo messageCenterOnlineRepo;
  @Resource
  private JdbcOAuth2AuthorizationService jdbcOAuth2AuthorizationService;

  /**
   * Forces users offline based on receive object type.
   * 
   * <p>This method performs offline operations including:</p>
   * <ul>
   *   <li>Processing user-specific offline requests</li>
   *   <li>Handling tenant-wide offline requests</li>
   *   <li>Invalidating user access tokens</li>
   *   <li>Updating online status records</li>
   * </ul>
   * 
   * @param receiveObjectType Type of receive object (USER, TENANT)
   * @param receiveObjectIds List of object identifiers
   */
  @Override
  public void offline(ReceiveObjectType receiveObjectType, List<Long> receiveObjectIds) {
    if (isEmpty(receiveObjectIds)) {
      return;
    }

    if (receiveObjectType.equals(ReceiveObjectType.USER)) {
      // Force specific users offline
      offlineByUserIds(receiveObjectIds);

      // Invalidate user access tokens to force logout
      List<String> usernames = userRepo.findUsernamesByIdAndOnline(receiveObjectIds, true);
      jdbcOAuth2AuthorizationService.removeByPrincipalName(usernames);
    } else if (receiveObjectType.equals(ReceiveObjectType.TENANT)) {
      // Force all users in specific tenants offline
      List<Long> userIds = userRepo.findIdsByTenantIdAndOnline(receiveObjectIds, true);
      if (isNotEmpty(userIds)) {
        offlineByUserIds(userIds);

        // Invalidate user access tokens to force logout
        List<String> usernames = userRepo.findUsernamesByTenantIdAndOnline(receiveObjectIds, true);
        jdbcOAuth2AuthorizationService.removeByPrincipalName(usernames);
      }
    }
  }

  /**
   * Updates user online status based on session information.
   * 
   * <p>This method manages session-based status including:</p>
   * <ul>
   *   <li>Creating online records for new sessions</li>
   *   <li>Updating offline records for ended sessions</li>
   *   <li>Managing user online status</li>
   *   <li>Tracking session metadata</li>
   * </ul>
   * 
   * @param sessionId Session identifier
   * @param username User username
   * @param userAgent User agent string
   * @param deviceId Device identifier
   * @param remoteAddress Remote IP address
   * @param online Whether user is coming online
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void updateOnlineStatus(String sessionId, String username, String userAgent,
      String deviceId, String remoteAddress, boolean online) {
    User user = userRepo.findAllByUsername(username).get(0);
    if (isNull(user)) {
      return;
    }

    if (online) {
      // Create online session record
      onlineBySession(sessionId, userAgent, deviceId, remoteAddress, user);
    } else {
      // Update offline session record
      offlineBySession(sessionId, user.getId());
    }
  }

  /**
   * Handles application shutdown by updating all online users to offline.
   * 
   * <p>This method performs shutdown cleanup including:</p>
   * <ul>
   *   <li>Retrieving all online usernames</li>
   *   <li>Finding corresponding user IDs</li>
   *   <li>Updating all users to offline status</li>
   * </ul>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void shutdown() {
    // Get all online usernames from local cache
    Collection<String> usernames = LOCAL_ONLINE_USERS.values();
    if (isNotEmpty(usernames)) {
      // Find user IDs and update offline status
      List<Long> userIds = userRepo.findIdsByUsernameIn(usernames);
      if (isEmpty(userIds)) {
        userRepo.updateOfflineStatus(userIds);
      }
    }
  }

  /**
   * Forces specific users offline by user IDs.
   * 
   * @param userIds List of user identifiers to force offline
   */
  private void offlineByUserIds(List<Long> userIds) {
    List<User> users = userRepo.findByIdIn(userIds);
    if (isEmpty(users)) {
      offlineByUserId(users.stream().map(User::getId).collect(Collectors.toSet()));
    }
  }

  /**
   * Creates online session record and updates user status.
   * 
   * @param sessionId Session identifier
   * @param userAgent User agent string
   * @param deviceId Device identifier
   * @param remoteAddress Remote IP address
   * @param user User entity
   */
  private void onlineBySession(String sessionId, String userAgent, String deviceId, String remoteAddress,
      User user) {
    // Save online session record
    insert0(assembleMessageCenterOnline(sessionId, user, userAgent, deviceId, remoteAddress));
    // Update user online status
    userRepo.updateOnlineStatus(List.of(user.getId()));
  }

  /**
   * Updates offline session record and user status.
   * 
   * @param sessionId Session identifier
   * @param userId User identifier
   */
  private void offlineBySession(String sessionId, Long userId) {
    // Update offline session record
    messageCenterOnlineRepo.updateOfflineBySessionIdAndUserId(sessionId, userId);
    // Update user offline status
    userRepo.updateOfflineStatus(List.of(userId));
  }

  /**
   * Forces users offline by user IDs.
   * 
   * @param userIds Collection of user identifiers
   */
  private void offlineByUserId(Collection<Long> userIds) {
    // Update offline session records
    messageCenterOnlineRepo.updateOfflineByUserIdIn(userIds);
    // Update user offline status
    userRepo.updateOfflineStatus(userIds);
  }

  @Override
  protected BaseRepository<MessageCenterOnline, Long> getRepository() {
    return this.messageCenterOnlineRepo;
  }
}
