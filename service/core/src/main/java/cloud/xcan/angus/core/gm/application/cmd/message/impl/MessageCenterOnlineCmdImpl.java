package cloud.xcan.angus.core.gm.application.cmd.message.impl;

import static cloud.xcan.angus.core.gm.application.converter.MessageCenterConverter.assembleMessageCenterOnline;
import static cloud.xcan.angus.core.gm.infra.message.MessageCenterConnectionListener.LOCAL_ONLINE_USERS;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;

import cloud.xcan.angus.api.commonlink.mcenter.MessageCenterOnline;
import cloud.xcan.angus.api.commonlink.mcenter.MessageCenterOnlineRepo;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.commonlink.user.UserRepo;
import cloud.xcan.angus.api.enums.ReceiveObjectType;
import cloud.xcan.angus.api.manager.UserManager;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.message.MessageCenterOnlineCmd;
import cloud.xcan.angus.core.gm.application.query.user.impl.UserQueryImpl;
import cloud.xcan.angus.core.gm.infra.message.MessageCenterNoticeMessage;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.security.authentication.service.JdbcOAuth2AuthorizationService;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
  public void offline(MessageCenterNoticeMessage message) {
    List<Long> objectIds = message.getContent().getReceiveObjectIds();
    if (isEmpty(objectIds)) {
      return;
    }

    if (message.getReceiveObjectType().equals(ReceiveObjectType.USER)) {
      offline0(objectIds);

      // Invalidate user access token to force logout.
      List<String> usernames = userRepo.findUsernamesByIdAndOnline(objectIds, true);
      jdbcOAuth2AuthorizationService.removeByPrincipalName(usernames);
    } else if (message.getReceiveObjectType().equals(ReceiveObjectType.TENANT)) {
      List<Long> userIds = userRepo.findIdsByTenantIdAndOnline(objectIds, true);
      if (isNotEmpty(userIds)) {
        offline0(userIds);

        // Invalidate user access token to force logout.
        List<String> usernames = userRepo.findUsernamesByTenantIdAndOnline(objectIds, true);
        jdbcOAuth2AuthorizationService.removeByPrincipalName(usernames);
      }
    }
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void updateOnlineStatus(String username, String userAgent, String deviceId,
      String remoteAddress, Boolean online) {
    User user = userRepo.findAllByUsername(username).get(0);
    if (isNull(user)) {
      return;
    }

    if (online) {
      online0(userAgent, deviceId, remoteAddress, user);
    } else {
      offline0(userAgent, deviceId, remoteAddress, user);
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

  private void offline0(List<Long> objectIds) {
    List<User> users = userRepo.findByIdIn(objectIds);
    if (isEmpty(users)) {
      for (User user : users) {
        offline0(null, null, null, user);
      }
    }
  }

  private void online0(String userAgent, String deviceId, String remoteAddress, User user) {
    // Save online records.
    insert0(assembleMessageCenterOnline(user, userAgent, deviceId, remoteAddress, true));
    // Update user online status.
    userRepo.updateOnlineStatus(List.of(user.getId()));
  }

  private void offline0(String userAgent, String deviceId, String remoteAddress, User user) {
    // Save offline records.
    insert0(assembleMessageCenterOnline(user, userAgent, deviceId, remoteAddress, false));
    // Update user offline status.
    userRepo.updateOfflineStatus(List.of(user.getId()));
  }

  @Override
  protected BaseRepository<MessageCenterOnline, Long> getRepository() {
    return this.messageCenterOnlineRepo;
  }
}
