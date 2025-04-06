package cloud.xcan.angus.core.gm.application.cmd.event;

import cloud.xcan.angus.core.gm.domain.channel.EventChannel;
import cloud.xcan.angus.core.gm.domain.event.push.EventPush;
import cloud.xcan.angus.spec.experimental.IdKey;


public interface EventChannelCmd {

  IdKey<Long, Object> add(EventChannel channel);

  IdKey<Long, Object> replace(EventChannel channel);

  void delete(Long id);

  void channelTest(EventPush eventPush);

}
