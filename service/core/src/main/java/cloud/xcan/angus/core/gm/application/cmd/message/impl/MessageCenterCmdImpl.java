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
import cloud.xcan.angus.api.pojo.Message;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.cmd.message.MessageCenterCmd;
import cloud.xcan.angus.core.gm.application.cmd.message.MessageCenterOnlineCmd;
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

/**
 * Implementation of message center command operations for managing message notifications.
 *
 * <p>This class provides comprehensive functionality for message center management including:</p>
 * <ul>
 *   <li>Sending push notifications via WebSocket</li>
 *   <li>Managing offline user notifications</li>
 *   <li>Handling broadcast messages across instances</li>
 *   <li>Routing messages to appropriate recipients</li>
 * </ul>
 *
 * <p>The implementation ensures proper message delivery with broadcast support
 * and recipient filtering based on object types.</p>
 */
@Slf4j
@org.springframework.stereotype.Service
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

  /**
   * Pushes message notifications to recipients.
   *
   * <p>This method performs message pushing including:</p>
   * <ul>
   *   <li>Validating broadcast parameters</li>
   *   <li>Broadcasting messages across instances</li>
   *   <li>Sending local WebSocket messages</li>
   *   <li>Handling broadcast exceptions</li>
   * </ul>
   *
   * @param dto Message center push data
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void push(MessageCenterPushDto dto) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        if (dto.isBroadcast()) {
          // Validate required parameters for broadcast
          checkRequiredParam(dto.getReceiveObjectType(), dto.getReceiveObjectIds());
        }
      }

      @Override
      protected Void process() {
        if (dto.isBroadcast()) {
          // Broadcast message to all instances
          dto.setBroadcast(false);
          String offlineEndpoint = "/api/v1/message/center/push";
          try {
            feignBroadcastInvoker.broadcast(applicationInfo.getArtifactId(), offlineEndpoint, dto);
          } catch (Throwable e) {
            log.error("Broadcast notice messages to all instances exception: ", e);
          }
        } else {
          // Send local WebSocket message
          sendLocalWebSocketMessage(pushToNoticeDomain(dto));
        }
        return null;
      }
    }.execute();
  }

  /**
   * Sends offline notifications to users.
   *
   * <p>This method performs offline notification including:</p>
   * <ul>
   *   <li>Validating broadcast parameters</li>
   *   <li>Broadcasting offline messages across instances</li>
   *   <li>Processing local offline notifications</li>
   *   <li>Handling broadcast exceptions</li>
   * </ul>
   *
   * @param dto Message center offline data
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void offline(MessageCenterOfflineDto dto) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        if (dto.isBroadcast()) {
          // Validate required parameters for broadcast
          checkRequiredParam(dto.getReceiveObjectType(), dto.getReceiveObjectIds());
        }
      }

      @Override
      protected Void process() {
        if (dto.isBroadcast()) {
          // Broadcast offline message to all instances
          dto.setBroadcast(false);
          String offlineEndpoint = "/api/v1/message/center/online/off";
          try {
            feignBroadcastInvoker.broadcast(applicationInfo.getArtifactId(), offlineEndpoint, dto);
          } catch (Throwable e) {
            log.error("Broadcast offline messages to all instances exception: ", e);
          }
        } else {
          // Process local offline notification
          messageCenterOnlineCmd.offline(dto.getReceiveObjectType(), dto.getReceiveObjectIds());
        }
        return null;
      }
    }.execute();
  }

  /**
   * Sends local WebSocket messages based on receive object type.
   *
   * <p>This method routes messages to appropriate recipients including:</p>
   * <ul>
   *   <li>All users in the system</li>
   *   <li>Users within specific tenants</li>
   *   <li>Users within specific departments</li>
   *   <li>Users within specific groups</li>
   *   <li>Specific users by ID</li>
   *   <li>Users with specific policy roles</li>
   * </ul>
   *
   * @param message Message to send
   */
  public void sendLocalWebSocketMessage(Message message) {
    ReceiveObjectType receiveObject = message.getReceiveObjectType();

    switch (receiveObject) {
      case ALL: {
        // Send to all online users
        List<String> onlineUsernames = userRepo.findUsernamesByOnline(true);
        messageNoticeService.sendUserMessage(onlineUsernames, message);
      }
      case TENANT: {
        // Send to online users within specific tenants
        List<Long> tenantIds = message.getReceiveObjectIds();
        List<String> onlineUsernames = userRepo.findUsernamesByTenantIdAndOnline(tenantIds, true);
        messageNoticeService.sendUserMessage(onlineUsernames, message);
      }
      case DEPT: {
        // Send to online users within specific departments
        List<Long> deptIds = message.getReceiveObjectIds();
        Set<String> onlineUsernames = deptUserRepo.findUsernamesByDeptIdInAndOnline(deptIds, true);
        messageNoticeService.sendUserMessage(onlineUsernames, message);
      }
      case GROUP: {
        // Send to online users within specific groups
        List<Long> groupIds = message.getReceiveObjectIds();
        Set<String> onlineUsernames = groupUserRepo.findUsernamesByGroupIdInAndOnline(groupIds,
            true);
        messageNoticeService.sendUserMessage(onlineUsernames, message);
      }
      case USER: {
        // Send to specific online users
        List<Long> userIds = message.getReceiveObjectIds();
        List<String> onlineUsernames = userRepo.findUsernamesByIdAndOnline(userIds, true);
        messageNoticeService.sendUserMessage(onlineUsernames, message);
      }
      case TO_POLICY: {
        // Send to users with specific policy roles
        List<Long> topRoleIds = message.getReceiveObjectIds();
        Set<Long> roleUserIds = toRoleUserRepo.findAllByToRoleIdIn(topRoleIds)
            .stream().map(TORoleUser::getUserId).collect(Collectors.toSet());
        if (isNotEmpty(roleUserIds)) {
          List<String> onlineUsernames = userRepo.findUsernamesByIdAndOnline(roleUserIds, true);
          messageNoticeService.sendUserMessage(onlineUsernames, message);
        }
      }
      default: {
        // No action for unsupported object types
      }
    }
  }

  /**
   * Validates required parameters for broadcast operations.
   *
   * @param objectType Receive object type
   * @param objectIds  Receive object identifiers
   */
  private void checkRequiredParam(ReceiveObjectType objectType, List<Long> objectIds) {
    if (!objectType.equals(ReceiveObjectType.ALL) && isEmpty(objectIds)) {
      throw ProtocolException.of(QUERY_FIELD_EMPTY_T, new Object[]{"objectIds"});
    }
  }
}
