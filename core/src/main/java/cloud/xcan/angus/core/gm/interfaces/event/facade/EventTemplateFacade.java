package cloud.xcan.angus.core.gm.interfaces.event.facade;

import cloud.xcan.angus.core.gm.interfaces.event.facade.dto.template.EventTemplateAddDto;
import cloud.xcan.angus.core.gm.interfaces.event.facade.dto.template.EventTemplateChannelReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.event.facade.dto.template.EventTemplateFindDto;
import cloud.xcan.angus.core.gm.interfaces.event.facade.dto.template.EventTemplateReceiverDto;
import cloud.xcan.angus.core.gm.interfaces.event.facade.dto.template.EventTemplateReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.event.facade.dto.template.EventTemplateSearchDto;
import cloud.xcan.angus.core.gm.interfaces.event.facade.vo.template.EventTemplateCurrentDetailVo;
import cloud.xcan.angus.core.gm.interfaces.event.facade.vo.template.EventTemplateVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;


public interface EventTemplateFacade {

  IdKey<Long, Object> add(EventTemplateAddDto dto);

  IdKey<Long, Object> replace(EventTemplateReplaceDto dto);

  void channelReplace(EventTemplateChannelReplaceDto dto);

  void receiverReplace(EventTemplateReceiverDto dto);

  void delete(Long id);

  EventTemplateVo detail(Long id);

  EventTemplateCurrentDetailVo currentDetail(Long id);

  PageResult<EventTemplateVo> list(EventTemplateFindDto dto);

  PageResult<EventTemplateCurrentDetailVo> currentList(EventTemplateFindDto dto);

  PageResult<EventTemplateVo> search(EventTemplateSearchDto dto);

  PageResult<EventTemplateCurrentDetailVo> currentSearch(EventTemplateSearchDto dto);

  //List<EventTemplateCurrentDetailVo> angusTesterExecTemplateList();

}
