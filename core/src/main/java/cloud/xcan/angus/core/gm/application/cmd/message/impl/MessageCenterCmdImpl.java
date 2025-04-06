package cloud.xcan.angus.core.gm.application.cmd.message.impl;

import static cloud.xcan.angus.core.gm.infra.message.MessageCenterNoticeMessage.MESSAGE_CENTER_NOTICE;
import static cloud.xcan.angus.core.gm.infra.message.MessageCenterNoticeMessage.MESSAGE_CENTER_SIGN_OUT;
import static cloud.xcan.angus.remote.message.CommProtocolException.M.QUERY_FIELD_EMPTY_T;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.to.TORoleUser;
import cloud.xcan.angus.api.commonlink.to.TORoleUserRepo;
import cloud.xcan.angus.api.commonlink.user.UserRepo;
import cloud.xcan.angus.api.commonlink.user.dept.DeptUserRepo;
import cloud.xcan.angus.api.commonlink.user.group.GroupUserRepo;
import cloud.xcan.angus.api.enums.ReceiveObjectType;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.cmd.message.MessageCenterCmd;
import cloud.xcan.angus.core.gm.application.cmd.message.MessageCenterOnlineCmd;
import cloud.xcan.angus.core.gm.infra.message.MessageCenterNoticeMessage;
import cloud.xcan.angus.core.gm.infra.message.MessageCenterNoticeService;
import cloud.xcan.angus.core.utils.GsonUtils;
import cloud.xcan.angus.remote.message.CommProtocolException;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Biz
public class MessageCenterCmdImpl implements MessageCenterCmd {

  @Resource
  private UserRepo userRepo;

  @Resource
  private DeptUserRepo deptUserRepo;

  @Resource
  private GroupUserRepo groupUserRepo;

  @Resource
  private TORoleUserRepo toRoleUserRepo;

  @Resource
  private MessageCenterOnlineCmd messageCenterOnlineCmd;

  @Resource
  private MessageCenterNoticeService messageCenterNoticeService;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void send(MessageCenterNoticeMessage message) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        checkRequiredParam(message.getReceiveObjectType(),
            message.getContent().getReceiveObjectIds());
      }

      @Override
      protected Void process() {
        if (MESSAGE_CENTER_NOTICE.equals(message.getBizKey())) {
          sendNoticeMessage(message);
        } else if (MESSAGE_CENTER_SIGN_OUT.equals(message.getBizKey())) {
          messageCenterOnlineCmd.offline(message);
        }
        return null;
      }
    }.execute();
  }

  @Override
  public void sendNoticeMessage(MessageCenterNoticeMessage message) {
    ReceiveObjectType receiveObject = message.getReceiveObjectType();

    switch (receiveObject) {
      case ALL: {
        List<String> onlineUsernames = userRepo.findUsernamesByOnline(true);
        sendMessage(message, onlineUsernames);
      }
      case TENANT: {
        List<Long> tenantIds = message.getContent().getReceiveObjectIds();
        List<String> onlineUsernames = userRepo.findUsernamesByTenantIdAndOnline(
            tenantIds, true);
        sendMessage(message, onlineUsernames);
      }
      case DEPT: {
        List<Long> deptIds = message.getContent().getReceiveObjectIds();
        Set<String> onlineUsernames = deptUserRepo.findUsernamesByDeptIdInAndOnline(
            deptIds, true);
        sendMessage(message, onlineUsernames);
      }
      case GROUP: {
        List<Long> groupIds = message.getContent().getReceiveObjectIds();
        Set<String> onlineUsernames = groupUserRepo.findUsernamesByGroupIdInAndOnline(
            groupIds, true);
        sendMessage(message, onlineUsernames);
      }
      case USER: {
        List<Long> userIds = message.getContent().getReceiveObjectIds();
        List<String> onlineUsernames = userRepo.findUsernamesByIdAndOnline(userIds, true);
        sendMessage(message, onlineUsernames);
      }
      case TO_POLICY: {
        List<Long> topRoleIds = message.getContent().getReceiveObjectIds();
        Set<Long> roleUserIds = toRoleUserRepo.findAllByToRoleIdIn(topRoleIds)
            .stream().map(TORoleUser::getUserId).collect(Collectors.toSet());
        if (isNotEmpty(roleUserIds)) {
          List<String> onlineUsernames = userRepo.findUsernamesByIdAndOnline(roleUserIds, true);
          sendMessage(message, onlineUsernames);
        }
      }
      default: {
        // None
      }
    }
  }

  private void sendMessage(MessageCenterNoticeMessage message, Collection<String> usernames) {
    if (isEmpty(usernames)) {
      return;
    }
    for (String username : usernames) {
      try {
        messageCenterNoticeService.sendUserMessage(username, GsonUtils.toJson(message));
      } catch (Exception e) {
        log.error("Send notice message to user[{}] exception: ", username, e);
      }
    }
  }

  private void checkRequiredParam(ReceiveObjectType objectType, List<Long> objectIds) {
    if (!objectType.equals(ReceiveObjectType.ALL) && isEmpty(objectIds)) {
      throw CommProtocolException.of(QUERY_FIELD_EMPTY_T, new Object[]{"objectIds"});
    }
  }

}
