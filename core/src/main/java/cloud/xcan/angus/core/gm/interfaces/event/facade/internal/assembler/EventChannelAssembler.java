package cloud.xcan.angus.core.gm.interfaces.event.facade.internal.assembler;

import cloud.xcan.angus.core.gm.domain.channel.EventChannel;
import cloud.xcan.angus.core.gm.interfaces.event.facade.dto.channel.EventChannelAddDto;
import cloud.xcan.angus.core.gm.interfaces.event.facade.dto.channel.EventChannelReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.event.facade.vo.channel.EventChannelVo;

public class EventChannelAssembler {

  public static EventChannel toAddDomain(EventChannelAddDto dto) {
    return new EventChannel().setType(dto.getChannelType())
        .setName(dto.getName())
        .setAddress(dto.getAddress());
  }

  public static EventChannel toReplaceDomain(EventChannelReplaceDto dto) {
    return new EventChannel().setId(dto.getId())
        .setType(dto.getChannelType())
        .setName(dto.getName())
        .setAddress(dto.getAddress());
  }

  public static EventChannelVo toVo(EventChannel eventChannel) {
    return new EventChannelVo().setId(eventChannel.getId())
        .setChannelType(eventChannel.getType())
        .setName(eventChannel.getName())
        .setAddress(eventChannel.getAddress());
  }
}
