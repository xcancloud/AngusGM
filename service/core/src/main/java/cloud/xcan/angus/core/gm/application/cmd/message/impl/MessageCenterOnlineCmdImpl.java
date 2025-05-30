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

  @Override
  public void offline(ReceiveObjectType receiveObjectType, List<Long> receiveObjectIds) {
    if (isEmpty(receiveObjectIds)) {
      return;
    }

    if (receiveObjectType.equals(ReceiveObjectType.USER)) {
      offlineByUserIds(receiveObjectIds);

      // Invalidate user access token to force logout.
      List<String> usernames = userRepo.findUsernamesByIdAndOnline(receiveObjectIds, true);
      jdbcOAuth2AuthorizationService.removeByPrincipalName(usernames);
    } else if (receiveObjectType.equals(ReceiveObjectType.TENANT)) {
      List<Long> userIds = userRepo.findIdsByTenantIdAndOnline(receiveObjectIds, true);
      if (isNotEmpty(userIds)) {
        offlineByUserIds(userIds);

        // Invalidate user access token to force logout.
        List<String> usernames = userRepo.findUsernamesByTenantIdAndOnline(receiveObjectIds, true);
        jdbcOAuth2AuthorizationService.removeByPrincipalName(usernames);
      }
    }
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void updateOnlineStatus(String sessionId, String username, String userAgent,
      String deviceId, String remoteAddress, boolean online) {
    User user = userRepo.findAllByUsername(username).get(0);
    if (isNull(user)) {
      return;
    }

    if (online) {
      onlineBySession(sessionId, userAgent, deviceId, remoteAddress, user);
    } else {
      offlineBySession(sessionId, user.getId());
    }
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void shutdown() {
    Collection<String> usernames = LOCAL_ONLINE_USERS.values();
    if (isNotEmpty(usernames)) {
      List<Long> userIds = userRepo.findIdsByUsernameIn(usernames);
      if (isEmpty(userIds)) {
        userRepo.updateOfflineStatus(userIds);
      }
    }
  }

  private void offlineByUserIds(List<Long> userIds) {
    List<User> users = userRepo.findByIdIn(userIds);
    if (isEmpty(users)) {
      offlineByUserId(users.stream().map(User::getId).collect(Collectors.toSet()));
    }
  }

  private void onlineBySession(String sessionId, String userAgent, String deviceId, String remoteAddress,
      User user) {
    // Save online records.
    insert0(assembleMessageCenterOnline(sessionId, user, userAgent, deviceId, remoteAddress));
    // Update user online status.
    userRepo.updateOnlineStatus(List.of(user.getId()));
  }

  private void offlineBySession(String sessionId, Long userId) {
    // Update offline records.
    messageCenterOnlineRepo.updateOfflineBySessionIdAndUserId(sessionId, userId);
    // Update user offline status.
    userRepo.updateOfflineStatus(List.of(userId));
  }

  private void offlineByUserId(Collection<Long> userIds) {
    // Update offline records.
    messageCenterOnlineRepo.updateOfflineByUserIdIn(userIds);
    // Update user offline status.
    userRepo.updateOfflineStatus(userIds);
  }

  @Override
  protected BaseRepository<MessageCenterOnline, Long> getRepository() {
    return this.messageCenterOnlineRepo;
  }
}
