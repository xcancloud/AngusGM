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
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.message.MessageCenterOnlineCmd;
import cloud.xcan.angus.core.gm.infra.message.MessageCenterNoticeMessage;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Biz
public class MessageCenterOnlineCmdImpl extends CommCmd<MessageCenterOnline, Long> implements
    MessageCenterOnlineCmd {

  @Resource
  private UserRepo userRepo;

  @Resource
  private MessageCenterOnlineRepo messageCenterOnlineRepo;

  @Override
  public void offline(MessageCenterNoticeMessage message) {
    new BizTemplate<Void>(false) {

      @Override
      protected Void process() {
        List<Long> objectIds = message.getContent().getReceiveObjectIds();
        if (isEmpty(objectIds)) {
          return null;
        }

        if (message.getReceiveObjectType().equals(ReceiveObjectType.USER)) {
          updateOfflineStatus0(objectIds);

          // TODO Invalidate user access token to force logout.
        } else if (message.getReceiveObjectType().equals(ReceiveObjectType.TENANT)) {
          List<Long> userIds = userRepo.findIdsByTenantIdAndOnline(objectIds, true);
          if (isNotEmpty(userIds)) {
            updateOfflineStatus0(userIds);

            // TODO Invalidate user access token to force logout.
          }
        }
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void updateOnlineStatus(String username, Boolean online) {
    new BizTemplate<Void>(false) {

      @Override
      protected Void process() {
        User user = userRepo.findAllByUsername(username).get(0);
        if (isNull(user)) {
          return null;
        }

        updateOnlineStatus0(online, user);
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void updateOnlineStatus(Long userId, Boolean online) {
    new BizTemplate<Void>(false) {

      @Override
      protected Void process() {
        User user = userRepo.findUserByUserId(userId);
        if (isNull(user)) {
          return null;
        }

        updateOnlineStatus0(online, user);
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void shutdown() {
    new BizTemplate<Void>(false) {

      @Override
      protected Void process() {
        Collection<String> usernames = LOCAL_ONLINE_USERS.values();
        if (isNotEmpty(usernames)) {
          List<Long> userIds = userRepo.findIdsByUsernameIn(usernames);
          updateOfflineStatus0(userIds);
        }
        return null;
      }
    }.execute();
  }

  private void updateOnlineStatus0(Boolean online, User user) {
    if (online) {
      // Save online records.
      insert0(assembleMessageCenterOnline(user));
      // Update user online status.
      userRepo.updateOnlineStatus(List.of(user.getId()));
    } else {
      // Update user offline status.
      updateOfflineStatus0(List.of(user.getId()));
    }
  }

  private void updateOfflineStatus0(List<Long> userIds) {
    if (isNotEmpty(userIds)) {
      messageCenterOnlineRepo.updateOfflineStatus(userIds);
      userRepo.updateOfflineStatus(userIds);
    }
  }

  @Override
  protected BaseRepository<MessageCenterOnline, Long> getRepository() {
    return this.messageCenterOnlineRepo;
  }
}
