package cloud.xcan.angus.core.gm.application.cmd.event.impl;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.event.EventTemplateReceiverCmd;
import cloud.xcan.angus.core.gm.application.query.event.EventTemplateQuery;
import cloud.xcan.angus.core.gm.domain.email.template.EventTemplate;
import cloud.xcan.angus.core.gm.domain.event.template.receiver.EventTemplateReceiver;
import cloud.xcan.angus.core.gm.domain.event.template.receiver.EventTemplateReceiverRepo;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;

@Biz
public class EventTemplateReceiverCmdImpl extends CommCmd<EventTemplateReceiver, Long>
    implements EventTemplateReceiverCmd {

  @Resource
  private EventTemplateReceiverRepo eventTemplateReceiverRepo;

  @Resource
  private EventTemplateQuery eventTemplateQuery;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void receiverReplace(EventTemplateReceiver receiver) {
    new BizTemplate<Void>() {
      EventTemplate templateDb;

      @Override
      protected void checkParams() {
        // Check the event template exists
        templateDb = eventTemplateQuery.checkAndFind(receiver.getTemplateId());
      }

      @Override
      protected Void process() {
        // Delete existed or clear empty template receivers
        eventTemplateReceiverRepo.deleteAllByTemplateId(receiver.getTemplateId());

        // Save new template receivers
        if (isNotEmpty(receiver.getNoticeTypes())
            && (isNotEmpty(receiver.getReceiverTypes()) || isNotEmpty(receiver.getReceiverIds()))) {
          insert0(receiver);
        }
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<EventTemplateReceiver, Long> getRepository() {
    return eventTemplateReceiverRepo;
  }
}
