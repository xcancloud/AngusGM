package cloud.xcan.angus.core.gm.application.cmd.message.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertForbidden;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.gm.application.converter.MessageConverter.messageToPushDto;
import static cloud.xcan.angus.core.gm.application.converter.MessageConverter.toMessageSent;
import static cloud.xcan.angus.core.gm.application.converter.MessageConverter.toSendEmailMessage;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isOpClient;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isTenantClient;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.setOptTenantId;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getTenantId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.lengthSafe;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.enums.ReceiveObjectType;
import cloud.xcan.angus.api.manager.UserManager;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.email.EmailCmd;
import cloud.xcan.angus.core.gm.application.cmd.message.MessageCenterCmd;
import cloud.xcan.angus.core.gm.application.cmd.message.MessageCmd;
import cloud.xcan.angus.core.gm.domain.message.Message;
import cloud.xcan.angus.core.gm.domain.message.MessageCurrentRepo;
import cloud.xcan.angus.core.gm.domain.message.MessageRepo;
import cloud.xcan.angus.core.gm.domain.message.MessageSent;
import cloud.xcan.angus.core.gm.domain.message.MessageStatus;
import cloud.xcan.angus.core.gm.domain.message.ReceiveObject;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.remote.message.AbstractResultMessageException;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
public class MessageCmdImpl extends CommCmd<Message, Long> implements MessageCmd {

  @Resource
  private MessageRepo messageRepo;

  @Resource
  private MessageCurrentRepo messageCurrentRepo;

  @Resource
  private UserManager userManager;

  @Resource
  private MessageCenterCmd messageCenterCmd;

  @Resource
  private EmailCmd emailCmd;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public IdKey<Long, Object> add(Message message) {
    return new BizTemplate<IdKey<Long, Object>>() {
      @Override
      protected void checkParams() {
        assertTrue(isOpClient() || (nonNull(message.getReceiveTenantId())
                && getTenantId().equals(message.getReceiveTenantId())),
            "Deny operation to other tenant");
        assertTrue(ReceiveObjectType.ALL.equals(message.getReceiveObjectType())
                || isNotEmpty(message.getReceiveObjectData()),
            "receiveObjects cannot be empty when receiveObjectType is not ALL");
        assertForbidden(ReceiveObjectType.ALL.equals(message.getReceiveObjectType())
                || nonNull(message.getReceiveTenantId()),
            "receiveTenantId cannot be empty when receiveObjectType is not ALL");
      }

      @Override
      protected IdKey<Long, Object> process() {
        setOptTenantId(getTenantId());
        if (isTenantClient()) {
          message.setReceiveTenantId(getTenantId());
        }
        if (isOpClient() && message.getReceiveObjectType().equals(ReceiveObjectType.ALL)) {
          message.setReceiveTenantId(null).setReceiveObjectData(null);
        }
        return insert(message);
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(Set<Long> ids) {
    new BizTemplate<Void>() {

      @Override
      protected Void process() {
        messageRepo.updateDeletedByIdIn(ids, LocalDateTime.now());
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void sentInSiteMessage(Message message) {
    new BizTemplate<Void>() {

      @Override
      protected Void process() {
        String failureReason = null;
        try {
          // Sent message by WebSocket
          messageCenterCmd.push(messageToPushDto(message));

          // Save user message
          Set<Long> allReceiveUserIds = getSentUserIds(message);
          if (isNotEmpty(allReceiveUserIds)) {
            List<MessageSent> messages = allReceiveUserIds.stream().map(
                    userId -> toMessageSent(message, userId, uidGenerator.getUID()))
                .collect(Collectors.toList());
            messageCurrentRepo.batchInsert0(messages);
          }

          // Save message status
          message.setStatus(MessageStatus.SENT).setSendDate(LocalDateTime.now())
              .setSentNum(allReceiveUserIds.size());
          messageRepo.save(message);
        } catch (Exception e) {
          log.error(e.getMessage(), e);
          if (e instanceof AbstractResultMessageException) {
            failureReason = e.getMessage();
          } else {
            failureReason = e.getMessage();
          }
        }

        if (nonNull(failureReason)) {
          message.setStatus(MessageStatus.FAILURE)
              .setFailureReason(lengthSafe(failureReason, 200));
          messageRepo.save(message);
        }
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void sentEmailMessage(Message message) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        String failureReason = null;
        try {
          // Sent message by email
          emailCmd.send(toSendEmailMessage(message), false);

          // Save message status
          Set<Long> allReceiveUserIds = getSentUserIds(message);
          message.setStatus(MessageStatus.SENT).setSendDate(LocalDateTime.now())
              .setSentNum(allReceiveUserIds.size());
          messageRepo.save(message);
        } catch (Exception e) {
          log.error(e.getMessage(), e);
          if (e instanceof AbstractResultMessageException) {
            failureReason = e.getMessage();
          } else {
            failureReason = e.getMessage();
          }
        }

        if (nonNull(failureReason)) {
          message.setStatus(MessageStatus.FAILURE)
              .setFailureReason(lengthSafe(failureReason, 200));
          messageRepo.save(message);
        }
        return null;
      }
    }.execute();
  }

  @Override
  public void plusReadNum(Set<Long> messageIds) {
    messageRepo.incrReadNum(messageIds);
  }

  private Set<Long> getSentUserIds(Message message) {
    Set<Long> allReceiveUserIds = new HashSet<>();
    switch (message.getReceiveObjectType()) {
      case ALL:
        allReceiveUserIds = message.isSentAllUsers() ? userManager.getAllValidUserIds()
            : userManager.getAllValidUserIdsByTenantId(message.getReceiveTenantId());
        break;
      case USER:
        allReceiveUserIds = message.getReceiveObjectData().stream()
            .map(ReceiveObject::getId).collect(Collectors.toSet());
        break;
      case GROUP:
        allReceiveUserIds = userManager.getValidUserIdsByGroupIds(message.getReceiveTenantId(),
            message.getReceiveObjectData().stream()
                .map(ReceiveObject::getId).collect(Collectors.toSet()));
        break;
      case DEPT:
        allReceiveUserIds = userManager.getValidUserIdsByDeptIds(message.getReceiveTenantId(),
            message.getReceiveObjectData().stream()
                .map(ReceiveObject::getId).collect(Collectors.toSet()));
        break;
      case TENANT:
        allReceiveUserIds = userManager
            .getAllValidUserIdsByTenantId(message.getReceiveTenantId());
        break;
      default:
        // NOOP
    }
    return allReceiveUserIds;
  }

  @Override
  protected BaseRepository<Message, Long> getRepository() {
    return messageRepo;
  }
}
