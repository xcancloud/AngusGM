package cloud.xcan.angus.core.gm.application.query.message.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.message.MessageQuery;
import cloud.xcan.angus.core.gm.domain.message.Message;
import cloud.xcan.angus.core.gm.domain.message.MessageInfo;
import cloud.xcan.angus.core.gm.domain.message.MessageInfoRepo;
import cloud.xcan.angus.core.gm.domain.message.MessageInfoSearchRepo;
import cloud.xcan.angus.core.gm.domain.message.MessageReceiveType;
import cloud.xcan.angus.core.gm.domain.message.MessageRepo;
import cloud.xcan.angus.core.gm.domain.message.MessageStatus;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.repository.summary.SummaryQueryRegister;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * <p>
 * Implementation of message query operations.
 * </p>
 * <p>
 * Manages message retrieval, validation, and pending message processing.
 * Provides comprehensive message querying with full-text search and summary support.
 * </p>
 * <p>
 * Supports message detail retrieval, paginated listing, full-text search,
 * and pending message queries for comprehensive message management.
 * </p>
 */
@Slf4j
@Biz
@SummaryQueryRegister(name = "Message", table = "message",
    groupByColumns = {"created_date", "receive_type", "status"})
public class MessageQueryImpl implements MessageQuery {

  @Resource
  private MessageRepo messageRepo;
  @Resource
  private MessageInfoRepo messageInfoRepo;
  @Resource
  private MessageInfoSearchRepo messageSearchRepo;

  /**
   * <p>
   * Retrieves detailed message information by ID.
   * </p>
   * <p>
   * Fetches complete message record with all associated information.
   * Throws ResourceNotFound exception if message does not exist.
   * </p>
   */
  @Override
  public Message detail(Long id) {
    return new BizTemplate<Message>() {

      @Override
      protected Message process() {
        return messageRepo.findById(id).orElseThrow(() -> ResourceNotFound.of(id, "Message"));
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves message information with optional filtering and search capabilities.
   * </p>
   * <p>
   * Supports full-text search and specification-based filtering.
   * Returns paginated results for comprehensive message management.
   * </p>
   */
  @Override
  public Page<MessageInfo> find(GenericSpecification<MessageInfo> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<MessageInfo>>() {

      @Override
      protected Page<MessageInfo> process() {
        return fullTextSearch
            ? messageSearchRepo.find(spec.getCriteria(), pageable, MessageInfo.class, match)
            : messageInfoRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves pending messages for processing.
   * </p>
   * <p>
   * Returns messages that are pending for processing with timing validation.
   * Limits results by size for processing control.
   * </p>
   */
  @Override
  public List<Message> getPendingMessage(MessageReceiveType receiveType, int size) {
    return new BizTemplate<List<Message>>(false) {

      @Override
      protected List<Message> process() {
        return messageRepo.findAllByReceiveTypeAndStatusAndTimingDateBefore(receiveType,
            MessageStatus.PENDING, LocalDateTime.now(), PageRequest.of(0, size));
      }
    }.execute();
  }
}
