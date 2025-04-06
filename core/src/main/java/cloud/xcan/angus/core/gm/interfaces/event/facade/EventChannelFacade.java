package cloud.xcan.angus.core.gm.interfaces.event.facade;


import cloud.xcan.angus.core.gm.domain.event.ReceiveChannelType;
import cloud.xcan.angus.core.gm.interfaces.event.facade.dto.channel.EventChannelAddDto;
import cloud.xcan.angus.core.gm.interfaces.event.facade.dto.channel.EventChannelReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.event.facade.dto.channel.EventChannelTestDto;
import cloud.xcan.angus.core.gm.interfaces.event.facade.vo.channel.EventChannelVo;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.List;


public interface EventChannelFacade {

  IdKey<Long, Object> add(EventChannelAddDto dto);

  IdKey<Long, Object> replace(EventChannelReplaceDto dto);

  void delete(Long id);

  List<EventChannelVo> channelList(ReceiveChannelType channelType);

  void channelTest(EventChannelTestDto dto);

}
