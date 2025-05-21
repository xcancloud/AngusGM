package cloud.xcan.angus.core.gm.application.cmd.message.impl;

import static cloud.xcan.angus.core.gm.application.converter.MessageCenterConverter.pushToNoticeDomain;
import static cloud.xcan.angus.remote.message.ProtocolException.M.QUERY_FIELD_EMPTY_T;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.to.TORoleUser;
import cloud.xcan.angus.api.commonlink.to.TORoleUserRepo;
import cloud.xcan.angus.api.commonlink.user.UserRepo;
import cloud.xcan.angus.api.commonlink.user.dept.DeptUserRepo;
import cloud.xcan.angus.api.commonlink.user.group.GroupUserRepo;
import cloud.xcan.angus.api.enums.ReceiveObjectType;
import cloud.xcan.angus.api.gm.message.dto.MessageCenterPushDto;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.cmd.message.MessageCenterCmd;
import cloud.xcan.angus.core.gm.application.cmd.message.MessageCenterOnlineCmd;
import cloud.xcan.angus.core.gm.infra.message.Message;
import cloud.xcan.angus.core.gm.infra.message.MessageNoticeService;
import cloud.xcan.angus.core.gm.interfaces.message.facade.dto.MessageCenterOfflineDto;
import cloud.xcan.angus.core.spring.boot.ApplicationInfo;
import cloud.xcan.angus.remote.client.HttpBroadcastInvoker;
import cloud.xcan.angus.remote.message.ProtocolException;
import jakarta.annotation.Resource;
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
  private MessageNoticeService messageNoticeService;

  @Resource
  private HttpBroadcastInvoker feignBroadcastInvoker;

  @Resource
  private ApplicationInfo applicationInfo;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void push(MessageCenterPushDto dto) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        if (dto.isBroadcast()) {
          checkRequiredParam(dto.getReceiveObjectType(), dto.getReceiveObjectIds());
        }
      }

      @Override
      protected Void process() {
        if (dto.isBroadcast()) {
          dto.setBroadcast(false);
          String offlineEndpoint = "/api/v1/message/center/push";
          try {
            feignBroadcastInvoker.broadcast(applicationInfo.getArtifactId(), offlineEndpoint, dto);
          } catch (Throwable e) {
            log.error("Broadcast notice messages to all instances exception: ", e);
          }
        } else {
          sendLocalWebSocketMessage(pushToNoticeDomain(dto));
        }
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void offline(MessageCenterOfflineDto dto) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        if (dto.isBroadcast()) {
          checkRequiredParam(dto.getReceiveObjectType(), dto.getReceiveObjectIds());
        }
      }

      @Override
      protected Void process() {
        if (dto.isBroadcast()) {
          dto.setBroadcast(false);
          String offlineEndpoint = "/api/v1/message/center/online/off";
          try {
            feignBroadcastInvoker.broadcast(applicationInfo.getArtifactId(), offlineEndpoint, dto);
          } catch (Throwable e) {
            log.error("Broadcast offline messages to all instances exception: ", e);
          }
        } else {
          messageCenterOnlineCmd.offline(dto.getReceiveObjectType(), dto.getReceiveObjectIds());
        }
        return null;
      }
    }.execute();
  }

  public void sendLocalWebSocketMessage(Message message) {
    ReceiveObjectType receiveObject = message.getReceiveObjectType();

    switch (receiveObject) {
      case ALL: {
        List<String> onlineUsernames = userRepo.findUsernamesByOnline(true);
        messageNoticeService.sendUserMessage(onlineUsernames, message);
      }
      case TENANT: {
        List<Long> tenantIds = message.getReceiveObjectIds();
        List<String> onlineUsernames = userRepo.findUsernamesByTenantIdAndOnline(tenantIds, true);
        messageNoticeService.sendUserMessage(onlineUsernames, message);
      }
      case DEPT: {
        List<Long> deptIds = message.getReceiveObjectIds();
        Set<String> onlineUsernames = deptUserRepo.findUsernamesByDeptIdInAndOnline(deptIds, true);
        messageNoticeService.sendUserMessage(onlineUsernames, message);
      }
      case GROUP: {
        List<Long> groupIds = message.getReceiveObjectIds();
        Set<String> onlineUsernames = groupUserRepo.findUsernamesByGroupIdInAndOnline(groupIds,
            true);
        messageNoticeService.sendUserMessage(onlineUsernames, message);
      }
      case USER: {
        List<Long> userIds = message.getReceiveObjectIds();
        List<String> onlineUsernames = userRepo.findUsernamesByIdAndOnline(userIds, true);
        messageNoticeService.sendUserMessage(onlineUsernames, message);
      }
      case TO_POLICY: {
        List<Long> topRoleIds = message.getReceiveObjectIds();
        Set<Long> roleUserIds = toRoleUserRepo.findAllByToRoleIdIn(topRoleIds)
            .stream().map(TORoleUser::getUserId).collect(Collectors.toSet());
        if (isNotEmpty(roleUserIds)) {
          List<String> onlineUsernames = userRepo.findUsernamesByIdAndOnline(roleUserIds, true);
          messageNoticeService.sendUserMessage(onlineUsernames, message);
        }
      }
      default: {
        // None
      }
    }
  }

  private void checkRequiredParam(ReceiveObjectType objectType, List<Long> objectIds) {
    if (!objectType.equals(ReceiveObjectType.ALL) && isEmpty(objectIds)) {
      throw ProtocolException.of(QUERY_FIELD_EMPTY_T, new Object[]{"objectIds"});
    }
  }

}
