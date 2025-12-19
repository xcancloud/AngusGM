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
import cloud.xcan.angus.core.gm.infra.remote.push.ChannelSendResponse;
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

/**
 * Implementation of event push command operations for managing event push notifications.
 *
 * <p>This class provides comprehensive functionality for event push management including:</p>
 * <ul>
 *   <li>Sending event notifications via various channels</li>
 *   <li>Managing push retry mechanisms</li>
 *   <li>Handling channel-specific address signing</li>
 *   <li>Updating push status and tracking</li>
 * </ul>
 *
 * <p>The implementation ensures proper event push processing with channel-specific
 * handling and status tracking.</p>
 */
@Slf4j
@Service
public class EventPushCmdImpl extends CommCmd<EventPush, Long> implements EventPushCmd {

  @Resource
  private EventPushRepo eventPushRepo;
  @Resource
  private EventRepo eventRepo;
  @Resource
  private HashMap<ReceiveChannelType, EventChannelPushCmd> pushServiceMap;

  /**
   * Sends event push notification via job processing.
   *
   * <p>This method performs push sending including:</p>
   * <ul>
   *   <li>Signing channel-specific addresses (e.g., DingTalk)</li>
   *   <li>Sending push via appropriate channel service</li>
   *   <li>Updating push status and retry count</li>
   *   <li>Managing event main status updates</li>
   * </ul>
   *
   * @param eventPush Event push data to send
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void sendByJob(EventPush eventPush) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        // Sign DingTalk address if applicable
        String address = signDingTalkAddress(eventPush);

        // Send event via appropriate channel type
        ChannelSendResponse result = pushServiceMap.get(eventPush.getChannelType())
            .push(eventPush);

        // Update push status and tracking information
        eventPush.setPush(result.isSuccess()).setPushMsg(result.getMessage());
        eventPush.setAddress(address);
        eventPush.setRetryTimes(eventPush.getRetryTimes() + 1);
        eventPushRepo.save(eventPush);

        // Important: Event is successful if at least one channel succeeds
        long count = eventRepo.countByIdAndPushStatus(eventPush.getEventId(),
            EventPushStatus.PUSH_SUCCESS);
        if (result.isSuccess() && count > 0) {
          return null;
        }

        // Update event main status based on push result
        eventRepo.updatePushStatusWhenNotSuccess(eventPush.getEventId(), result.isSuccess()
                ? EventPushStatus.PUSH_SUCCESS.getValue() : PUSH_FAIL.getValue(),
            result.getMessage());
        return null;
      }
    }.execute();
  }

  /**
   * Adds multiple event push records in batch.
   *
   * @param eventPushes List of event push records to add
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void add0(ArrayList<EventPush> eventPushes) {
    batchInsert(eventPushes);
  }

  /**
   * Updates multiple event push records in batch.
   *
   * @param eventPushes List of event push records to update
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void update0(List<EventPush> eventPushes) {
    batchUpdate0(eventPushes);
  }

  /**
   * Updates event push status to failure for specified events.
   *
   * @param eventIds Collection of event identifiers
   * @param failMsg  Failure message to set
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void updateEventPushFail(Collection<Long> eventIds, String failMsg) {
    // Update event main status to failure
    eventRepo.updatePushStatusWhenNotSuccess(eventIds, PUSH_FAIL.getValue(), failMsg);
  }

  /**
   * Signs DingTalk address with timestamp and signature if applicable.
   *
   * <p>This method adds DingTalk-specific security parameters including:</p>
   * <ul>
   *   <li>Timestamp for request validation</li>
   *   <li>Signature for security verification</li>
   * </ul>
   *
   * @param eventPush Event push data containing address and secret
   * @return Signed address with security parameters
   */
  private String signDingTalkAddress(EventPush eventPush) {
    String address = eventPush.getAddress();
    if (ReceiveChannelType.DINGTALK.equals(eventPush.getChannelType())
        && StringUtils.isNotEmpty(eventPush.getSecret())) {
      // Add timestamp and signature for DingTalk security
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
