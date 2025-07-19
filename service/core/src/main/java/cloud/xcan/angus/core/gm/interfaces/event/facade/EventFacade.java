package cloud.xcan.angus.core.gm.interfaces.event.facade;


import cloud.xcan.angus.core.event.source.EventContent;
import cloud.xcan.angus.core.gm.interfaces.event.facade.dto.EventFindDto;
import cloud.xcan.angus.core.gm.interfaces.event.facade.vo.EventDetailVo;
import cloud.xcan.angus.core.gm.interfaces.event.facade.vo.EventReceiveChannelVo;
import cloud.xcan.angus.core.gm.interfaces.event.facade.vo.EventVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.List;

public interface EventFacade {

  List<IdKey<Long, Object>> add(List<EventContent> eventContents);

  EventDetailVo detail(Long id);

  List<EventReceiveChannelVo> receiveChannel(String eventCode);

  PageResult<EventVo> list(EventFindDto dto);

}
