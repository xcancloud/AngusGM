package cloud.xcan.angus.core.gm.application.cmd.event.impl;


import static cloud.xcan.angus.core.gm.domain.event.push.EventPushStatus.PUSH_FAIL;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.event.EventChannelPushCmd;
import cloud.xcan.angus.core.gm.application.cmd.event.EventPushCmd;
import cloud.xcan.angus.core.gm.domain.event.EventRepo;
import cloud.xcan.angus.core.gm.domain.event.ReceiveChannelType;
import cloud.xcan.angus.core.gm.domain.event.push.EventPush;
import cloud.xcan.angus.core.gm.domain.event.push.EventPushRepo;
import cloud.xcan.angus.core.gm.domain.event.push.EventPushStatus;
import cloud.xcan.angus.core.gm.infra.remote.ChannelSendResponse;
import cloud.xcan.angus.core.gm.interfaces.event.facade.internal.assembler.EventAssembler;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.spec.utils.StringUtils;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
public class EventPushCmdImpl extends CommCmd<EventPush, Long> implements EventPushCmd {

  @Resource
  private EventPushRepo eventPushRepo;

  @Resource
  private EventRepo eventRepo;

  @Resource
  private HashMap<ReceiveChannelType, EventChannelPushCmd> pushServiceMap;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void sendByJob(EventPush eventPush) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        String address = signDingTalkAddress(eventPush);

        // Sent event by channel type
        ChannelSendResponse result = pushServiceMap.get(eventPush.getChannelType())
            .push(eventPush);

        // Save sent status
        eventPush.setPush(result.isSuccess()).setPushMsg(result.getMessage());
        eventPush.setAddress(address);
        eventPush.setRetryTimes(eventPush.getRetryTimes() + 1);
        eventPushRepo.save(eventPush);

        // Important:: As long as one channel is successful, the event is successful
        long count = eventRepo.countByIdAndPushStatus(eventPush.getEventId(),
            EventPushStatus.PUSH_SUCCESS);
        if (result.isSuccess() && count > 0) {
          return null;
        }

        // Update event main status
        eventRepo.updatePushStatusWhenNotSuccess(eventPush.getEventId(), result.isSuccess()
                ? EventPushStatus.PUSH_SUCCESS.getValue() : PUSH_FAIL.getValue(),
            result.getMessage());
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void add0(ArrayList<EventPush> eventPushes) {
    batchInsert(eventPushes);
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void update0(List<EventPush> eventPushes) {
    batchUpdate0(eventPushes);
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void updateEventPushFail(Collection<Long> eventIds, String failMsg) {
    // Update event main status
    eventRepo.updatePushStatusWhenNotSuccess(eventIds, PUSH_FAIL.getValue(), failMsg);
  }

  private String signDingTalkAddress(EventPush eventPush) {
    String address = eventPush.getAddress();
    if (ReceiveChannelType.DINGTALK.equals(eventPush.getChannelType())
        && StringUtils.isNotEmpty(eventPush.getSecret())) {
      long nowMillis = System.currentTimeMillis();
      eventPush.setAddress(address.concat("&timestamp=" + nowMillis).concat("&sign=")
          .concat(EventAssembler.getDingTalkSign(nowMillis, eventPush.getSecret())));
    }
    return address;
  }

  @Override
  protected BaseRepository<EventPush, Long> getRepository() {
    return eventPushRepo;
  }
}
