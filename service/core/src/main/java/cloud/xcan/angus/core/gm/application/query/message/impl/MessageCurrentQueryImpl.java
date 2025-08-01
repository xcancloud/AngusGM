package cloud.xcan.angus.core.gm.application.query.message.impl;

import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.message.MessageCurrentQuery;
import cloud.xcan.angus.core.gm.domain.message.MessageCurrentRepo;
import cloud.xcan.angus.core.gm.domain.message.MessageInfo;
import cloud.xcan.angus.core.gm.domain.message.MessageInfoRepo;
import cloud.xcan.angus.core.gm.domain.message.MessageReadTab;
import cloud.xcan.angus.core.gm.domain.message.MessageRepo;
import cloud.xcan.angus.core.gm.domain.message.MessageSent;
import cloud.xcan.angus.core.gm.interfaces.message.facade.vo.MessageStatusCountVo;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * Implementation of current message query operations.
 * </p>
 * <p>
 * Manages current user message retrieval, status counting, and message information association.
 * Provides comprehensive current message querying for user-specific message management.
 * </p>
 * <p>
 * Supports current user message detail retrieval, status counting,
 * and message information enrichment for comprehensive message management.
 * </p>
 */
@Slf4j
@Service
public class MessageCurrentQueryImpl implements MessageCurrentQuery {

  @Resource
  private MessageCurrentRepo messageCurrentRepo;
  @Resource
  private MessageRepo messageRepo;
  @Resource
  private MessageInfoRepo messageInfoRepo;

  /**
   * <p>
   * Retrieves detailed current user message information by ID.
   * </p>
   * <p>
   * Fetches complete message record for current user with message information association.
   * Throws ResourceNotFound exception if message does not exist for current user.
   * </p>
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public MessageSent detail(Long id) {
    return new BizTemplate<MessageSent>(false) {

      @Override
      protected MessageSent process() {
        MessageSent messageSent = messageCurrentRepo.findByIdAndReceiveUserId(id, getUserId())
            .orElseThrow(() -> ResourceNotFound.of(id, "MessageSent"));
        messageSent.setMessage(messageRepo.findById(messageSent.getMessageId())
            .orElse(null));
        return messageSent;
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves current user messages with specification-based filtering.
   * </p>
   * <p>
   * Supports dynamic filtering and pagination for comprehensive message management.
   * Enriches results with message information for complete display.
   * </p>
   */
  @Override
  public Page<MessageSent> list(Specification<MessageSent> spec, Pageable pageable) {
    return new BizTemplate<Page<MessageSent>>(false) {

      @Override
      protected Page<MessageSent> process() {
        Page<MessageSent> page = messageCurrentRepo.findAll(spec, pageable);
        if (page.hasContent()) {
          Map<Long, MessageInfo> messages = messageInfoRepo.findAllById(
                  page.getContent().stream().map(MessageSent::getMessageId)
                      .collect(Collectors.toList())).stream()
              .collect(Collectors.toMap(MessageInfo::getId, x -> x));
          for (MessageSent messageSent : page.getContent()) {
            messageSent.setMessageInfo(messages.get(messageSent.getMessageId()));
          }
        }
        return page;
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves message status count statistics for user.
   * </p>
   * <p>
   * Returns count statistics for all, read, and unread messages.
   * Provides comprehensive status overview for message management.
   * </p>
   */
  @Override
  public List<MessageStatusCountVo> statusCount(Long userId) {
    return new BizTemplate<List<MessageStatusCountVo>>() {

      @Override
      protected List<MessageStatusCountVo> process() {
        ArrayList<MessageStatusCountVo> messageStatusCountVos = new ArrayList<>(3);
        long readMessages = messageCurrentRepo.countByReceiveUserIdAndRead(userId, true);
        long noReadMessages = messageCurrentRepo.countByReceiveUserIdAndRead(userId, false);
        messageStatusCountVos.add(
            new MessageStatusCountVo().setCount(noReadMessages + readMessages)
                .setTab(MessageReadTab.ALL).setRead(null));
        messageStatusCountVos.add(
            new MessageStatusCountVo().setCount(readMessages).setTab(MessageReadTab.READ)
                .setRead(true));
        messageStatusCountVos.add(
            new MessageStatusCountVo().setCount(noReadMessages).setTab(MessageReadTab.UNREAD)
                .setRead(false));
        return messageStatusCountVos;
      }
    }.execute();
  }

}
