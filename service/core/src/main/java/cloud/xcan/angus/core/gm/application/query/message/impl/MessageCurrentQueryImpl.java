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


@Slf4j
@Service
public class MessageCurrentQueryImpl implements MessageCurrentQuery {

  @Resource
  private MessageCurrentRepo messageCurrentRepo;

  @Resource
  private MessageRepo messageRepo;

  @Resource
  private MessageInfoRepo messageInfoRepo;

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
