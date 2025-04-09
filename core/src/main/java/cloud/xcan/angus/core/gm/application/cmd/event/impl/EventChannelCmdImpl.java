package cloud.xcan.angus.core.gm.application.cmd.event.impl;

import static cloud.xcan.angus.core.utils.CoreUtils.copyPropertiesIgnoreTenantAuditing;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.ProtocolAssert;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.event.EventChannelCmd;
import cloud.xcan.angus.core.gm.application.cmd.event.EventChannelPushCmd;
import cloud.xcan.angus.core.gm.application.query.event.EventChannelQuery;
import cloud.xcan.angus.core.gm.domain.event.channel.EventChannel;
import cloud.xcan.angus.core.gm.domain.event.channel.EventChannelRepo;
import cloud.xcan.angus.core.gm.domain.event.ReceiveChannelType;
import cloud.xcan.angus.core.gm.domain.event.push.EventPush;
import cloud.xcan.angus.core.gm.infra.remote.ChannelSendResponse;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
public class EventChannelCmdImpl extends CommCmd<EventChannel, Long> implements EventChannelCmd {

  @Resource
  private EventChannelRepo eventChannelRepo;

  @Resource
  private EventChannelQuery eventChannelQuery;

  @Resource
  private HashMap<ReceiveChannelType, EventChannelPushCmd> eventChannelPushMap;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public IdKey<Long, Object> add(EventChannel channel) {
    return new BizTemplate<IdKey<Long, Object>>() {
      @Override
      protected void checkParams() {
        // Check the added channel
        eventChannelQuery.checkNameExisted(channel);
        eventChannelQuery.checkQuota(channel.getType(), 1);
      }

      @Override
      protected IdKey<Long, Object> process() {
        return insert(channel, "name");
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public IdKey<Long, Object> replace(EventChannel channel) {
    return new BizTemplate<IdKey<Long, Object>>() {
      EventChannel channelDb;

      @Override
      protected void checkParams() {
        // Check the updated channel
        if (nonNull(channel.getId())) {
          channelDb = eventChannelQuery.find(channel.getId());
          eventChannelQuery.checkNameExisted(channel);
        }
      }

      @Override
      protected IdKey<Long, Object> process() {
        // Add new channel.
        if (isNull(channel.getId())) {
          return add(channel);
        }

        // Update existed channel
        eventChannelRepo.save(copyPropertiesIgnoreTenantAuditing(channel, channelDb, "type"));
        return IdKey.of(channel.getId(), channel.getName());
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(Long id) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        eventChannelQuery.checkNotInUse(id);
      }

      @Override
      protected Void process() {
        eventChannelRepo.deleteById(id);
        return null;
      }
    }.execute();
  }

  @Override
  public void channelTest(EventPush eventPush) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        ChannelSendResponse response = eventChannelPushMap.get(eventPush.getChannelType())
            .push(eventPush);
        ProtocolAssert.assertTrue(response.isSuccess(), response.getMessage());
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<EventChannel, Long> getRepository() {
    return this.eventChannelRepo;
  }

}
