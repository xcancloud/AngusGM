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

/**
 * Implementation of message command operations for managing system messages.
 * 
 * <p>This class provides comprehensive functionality for message management including:</p>
 * <ul>
 *   <li>Creating and sending system messages</li>
 *   <li>Managing message delivery via WebSocket and email</li>
 *   <li>Handling message status tracking</li>
 *   <li>Managing message recipients and quotas</li>
 * </ul>
 * 
 * <p>The implementation ensures proper message delivery with status tracking
 * and recipient management across different delivery channels.</p>
 */
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

  /**
   * Creates a new message with comprehensive validation.
   * 
   * <p>This method performs message creation including:</p>
   * <ul>
   *   <li>Validating tenant access permissions</li>
   *   <li>Checking message recipient requirements</li>
   *   <li>Setting tenant context appropriately</li>
   *   <li>Creating message record</li>
   * </ul>
   * 
   * @param message Message to create
   * @return Created message identifier
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public IdKey<Long, Object> add(Message message) {
    return new BizTemplate<IdKey<Long, Object>>() {
      @Override
      protected void checkParams() {
        // Validate tenant access permissions
        assertTrue(isOpClient() || (nonNull(message.getReceiveTenantId())
                && getTenantId().equals(message.getReceiveTenantId())),
            "Deny operation to other tenant");
        // Validate message recipient requirements
        assertTrue(ReceiveObjectType.ALL.equals(message.getReceiveObjectType())
                || isNotEmpty(message.getReceiveObjectData()),
            "receiveObjects cannot be empty when receiveObjectType is not ALL");
        // Validate tenant ID requirements
        assertForbidden(ReceiveObjectType.ALL.equals(message.getReceiveObjectType())
                || nonNull(message.getReceiveTenantId()),
            "receiveTenantId cannot be empty when receiveObjectType is not ALL");
      }

      @Override
      protected IdKey<Long, Object> process() {
        // Set tenant context
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

  /**
   * Deletes messages by marking them as deleted.
   * 
   * @param ids Set of message identifiers to delete
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(Set<Long> ids) {
    new BizTemplate<Void>() {

      @Override
      protected Void process() {
        // Mark messages as deleted with timestamp
        messageRepo.updateDeletedByIdIn(ids, LocalDateTime.now());
        return null;
      }
    }.execute();
  }

  /**
   * Sends in-site messages via WebSocket with comprehensive tracking.
   * 
   * <p>This method performs in-site message delivery including:</p>
   * <ul>
   *   <li>Sending messages via WebSocket</li>
   *   <li>Creating user message records</li>
   *   <li>Updating message status and statistics</li>
   *   <li>Handling delivery failures</li>
   * </ul>
   * 
   * @param message Message to send
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void sentInSiteMessage(Message message) {
    new BizTemplate<Void>() {

      @Override
      protected Void process() {
        String failureReason = null;
        try {
          // Send message via WebSocket
          messageCenterCmd.push(messageToPushDto(message));

          // Create user message records
          Set<Long> allReceiveUserIds = getSentUserIds(message);
          if (isNotEmpty(allReceiveUserIds)) {
            List<MessageSent> messages = allReceiveUserIds.stream().map(
                    userId -> toMessageSent(message, userId, uidGenerator.getUID()))
                .collect(Collectors.toList());
            messageCurrentRepo.batchInsert0(messages);
          }

          // Update message status and statistics
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

        // Handle delivery failure
        if (nonNull(failureReason)) {
          message.setStatus(MessageStatus.FAILURE)
              .setFailureReason(lengthSafe(failureReason, 200));
          messageRepo.save(message);
        }
        return null;
      }
    }.execute();
  }

  /**
   * Sends email messages with comprehensive tracking.
   * 
   * <p>This method performs email message delivery including:</p>
   * <ul>
   *   <li>Sending messages via email service</li>
   *   <li>Updating message status and statistics</li>
   *   <li>Handling delivery failures</li>
   * </ul>
   * 
   * @param message Message to send
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void sentEmailMessage(Message message) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        String failureReason = null;
        try {
          // Send message via email service
          emailCmd.send(toSendEmailMessage(message), false);

          // Update message status and statistics
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

        // Handle delivery failure
        if (nonNull(failureReason)) {
          message.setStatus(MessageStatus.FAILURE)
              .setFailureReason(lengthSafe(failureReason, 200));
          messageRepo.save(message);
        }
        return null;
      }
    }.execute();
  }

  /**
   * Increments read count for specified messages.
   * 
   * @param messageIds Set of message identifiers to update read count
   */
  @Override
  public void plusReadNum(Set<Long> messageIds) {
    messageRepo.incrReadNum(messageIds);
  }

  /**
   * Retrieves user IDs for message recipients based on receive object type.
   * 
   * <p>This method determines recipients including:</p>
   * <ul>
   *   <li>All users in system or specific tenant</li>
   *   <li>Users within specific groups</li>
   *   <li>Users within specific departments</li>
   *   <li>Specific users by ID</li>
   * </ul>
   * 
   * @param message Message containing recipient information
   * @return Set of user identifiers for message recipients
   */
  private Set<Long> getSentUserIds(Message message) {
    Set<Long> allReceiveUserIds = new HashSet<>();
    switch (message.getReceiveObjectType()) {
      case ALL:
        // Get all valid users or users in specific tenant
        allReceiveUserIds = message.isSentAllUsers() ? userManager.getAllValidUserIds()
            : userManager.getAllValidUserIdsByTenantId(message.getReceiveTenantId());
        break;
      case USER:
        // Get specific users by ID
        allReceiveUserIds = message.getReceiveObjectData().stream()
            .map(ReceiveObject::getId).collect(Collectors.toSet());
        break;
      case GROUP:
        // Get users within specific groups
        allReceiveUserIds = userManager.getValidUserIdsByGroupIds(message.getReceiveTenantId(),
            message.getReceiveObjectData().stream()
                .map(ReceiveObject::getId).collect(Collectors.toSet()));
        break;
      case DEPT:
        // Get users within specific departments
        allReceiveUserIds = userManager.getValidUserIdsByDeptIds(message.getReceiveTenantId(),
            message.getReceiveObjectData().stream()
                .map(ReceiveObject::getId).collect(Collectors.toSet()));
        break;
      case TENANT:
        // Get all users in specific tenant
        allReceiveUserIds = userManager
            .getAllValidUserIdsByTenantId(message.getReceiveTenantId());
        break;
      default:
        // No action for unsupported object types
    }
    return allReceiveUserIds;
  }

  @Override
  protected BaseRepository<Message, Long> getRepository() {
    return messageRepo;
  }
}
