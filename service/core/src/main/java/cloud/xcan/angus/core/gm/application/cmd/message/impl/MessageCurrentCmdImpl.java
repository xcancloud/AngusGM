package cloud.xcan.angus.core.gm.application.cmd.message.impl;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.message.MessageCmd;
import cloud.xcan.angus.core.gm.application.cmd.message.MessageCurrentCmd;
import cloud.xcan.angus.core.gm.domain.message.MessageCurrentRepo;
import cloud.xcan.angus.core.gm.domain.message.MessageSent;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
public class MessageCurrentCmdImpl extends CommCmd<MessageSent, Long> implements MessageCurrentCmd {

  @Resource
  private MessageCurrentRepo messageCurrentRepo;

  @Resource
  private MessageCmd messageCmd;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(Set<Long> ids) {
    new BizTemplate<Void>() {

      @Override
      protected Void process() {
        messageCurrentRepo.updateDeletedByIdIn(ids, LocalDateTime.now());
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void read(Set<Long> ids) {
    new BizTemplate<Void>() {

      @Override
      protected Void process() {
        messageCurrentRepo.updateReadByIdIn(ids, LocalDateTime.now());
        messageCmd.plusReadNum(ids);
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<MessageSent, Long> getRepository() {
    return messageCurrentRepo;
  }

}
