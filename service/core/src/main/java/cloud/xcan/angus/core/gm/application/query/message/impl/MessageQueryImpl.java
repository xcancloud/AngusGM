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

  @Override
  public Message detail(Long id) {
    return new BizTemplate<Message>() {

      @Override
      protected Message process() {
        return messageRepo.findById(id).orElseThrow(() -> ResourceNotFound.of(id, "Message"));
      }
    }.execute();
  }

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
