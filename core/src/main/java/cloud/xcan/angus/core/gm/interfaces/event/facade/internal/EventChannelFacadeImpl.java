package cloud.xcan.angus.core.gm.interfaces.event.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.event.facade.internal.assembler.EventAssembler.testEventPush;
import static cloud.xcan.angus.core.gm.interfaces.event.facade.internal.assembler.EventChannelAssembler.toAddDomain;
import static cloud.xcan.angus.core.gm.interfaces.event.facade.internal.assembler.EventChannelAssembler.toReplaceDomain;

import cloud.xcan.angus.core.gm.application.cmd.event.EventChannelCmd;
import cloud.xcan.angus.core.gm.application.query.event.EventChannelQuery;
import cloud.xcan.angus.core.gm.domain.channel.EventChannel;
import cloud.xcan.angus.core.gm.domain.event.ReceiveChannelType;
import cloud.xcan.angus.core.gm.interfaces.event.facade.EventChannelFacade;
import cloud.xcan.angus.core.gm.interfaces.event.facade.dto.channel.EventChannelAddDto;
import cloud.xcan.angus.core.gm.interfaces.event.facade.dto.channel.EventChannelReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.event.facade.dto.channel.EventChannelTestDto;
import cloud.xcan.angus.core.gm.interfaces.event.facade.internal.assembler.EventChannelAssembler;
import cloud.xcan.angus.core.gm.interfaces.event.facade.vo.channel.EventChannelVo;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;


@Component
public class EventChannelFacadeImpl implements EventChannelFacade {

  @Resource
  private EventChannelCmd eventChannelCmd;

  @Resource
  private EventChannelQuery eventChannelQuery;

  @Override
  public IdKey<Long, Object> add(EventChannelAddDto dto) {
    return eventChannelCmd.add(toAddDomain(dto));
  }

  @Override
  public IdKey<Long, Object> replace(EventChannelReplaceDto dto) {
    return eventChannelCmd.replace(toReplaceDomain(dto));
  }

  @Override
  public void delete(Long id) {
    eventChannelCmd.delete(id);
  }

  @Override
  public List<EventChannelVo> channelList(ReceiveChannelType channelType) {
    List<EventChannel> eventChannels = eventChannelQuery.channelList(channelType);
    return eventChannels.stream().map(EventChannelAssembler::toVo).collect(Collectors.toList());
  }

  @Override
  public void channelTest(EventChannelTestDto dto) {
    eventChannelCmd.channelTest(testEventPush(dto));
  }

}
