package cloud.xcan.angus.core.gm.interfaces.event.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.event.facade.internal.assembler.EventAssembler.getSpecification;
import static cloud.xcan.angus.core.gm.interfaces.event.facade.internal.assembler.EventAssembler.toAddDomain;
import static cloud.xcan.angus.core.gm.interfaces.event.facade.internal.assembler.EventAssembler.toDetailVo;
import static cloud.xcan.angus.core.gm.interfaces.event.facade.internal.assembler.EventAssembler.toVo;
import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static java.util.Collections.emptyList;

import cloud.xcan.angus.core.event.source.EventContent;
import cloud.xcan.angus.core.gm.application.cmd.event.EventCmd;
import cloud.xcan.angus.core.gm.application.query.event.EventChannelQuery;
import cloud.xcan.angus.core.gm.application.query.event.EventQuery;
import cloud.xcan.angus.core.gm.application.query.event.EventSearch;
import cloud.xcan.angus.core.gm.application.query.event.EventTemplateQuery;
import cloud.xcan.angus.core.gm.domain.channel.EventChannel;
import cloud.xcan.angus.core.gm.domain.email.template.EventTemplate;
import cloud.xcan.angus.core.gm.domain.event.Event;
import cloud.xcan.angus.core.gm.domain.event.ReceiveChannelType;
import cloud.xcan.angus.core.gm.interfaces.event.facade.EventFacade;
import cloud.xcan.angus.core.gm.interfaces.event.facade.dto.EventFindDto;
import cloud.xcan.angus.core.gm.interfaces.event.facade.dto.EventSearchDto;
import cloud.xcan.angus.core.gm.interfaces.event.facade.internal.assembler.EventAssembler;
import cloud.xcan.angus.core.gm.interfaces.event.facade.internal.assembler.EventChannelAssembler;
import cloud.xcan.angus.core.gm.interfaces.event.facade.vo.EventDetailVo;
import cloud.xcan.angus.core.gm.interfaces.event.facade.vo.EventReceiveChannelVo;
import cloud.xcan.angus.core.gm.interfaces.event.facade.vo.EventVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;


@Component
public class EventFacadeImpl implements EventFacade {

  @Resource
  private EventQuery eventQuery;

  @Resource
  private EventCmd eventCmd;

  @Resource
  private EventSearch eventSearch;

  @Resource
  private EventTemplateQuery eventTemplateQuery;

  @Resource
  private EventChannelQuery eventChannelQuery;

  @Value("${xcan.event.gmApiUrlPrefix}")
  private String eventUrlPrefix;

  @Override
  public List<IdKey<Long, Object>> add(List<EventContent> eventContents) {
    return eventCmd.add(toAddDomain(eventContents));
  }

  @Override
  public EventDetailVo detail(Long id) {
    return toDetailVo(eventQuery.detail(id), eventUrlPrefix);
  }

  @Override
  public List<EventReceiveChannelVo> receiveChannel(String eventCode) {
    EventTemplate template = eventTemplateQuery.findByEventCode(eventCode);
    if (isEmpty(template)) {
      return emptyList();
    }
    List<EventChannel> eventChannels = eventChannelQuery.findByTemplateId(
        getOptTenantId(), template.getId());
    Map<ReceiveChannelType, List<EventChannel>> typeListMap = eventChannels.stream()
        .collect(Collectors.groupingBy(EventChannel::getType));
    return typeListMap.entrySet().stream()
        .map(e -> new EventReceiveChannelVo().setChannelType(e.getKey())
            .setChannels(e.getValue().stream().map(EventChannelAssembler::toVo).collect(
                Collectors.toList()))).collect(Collectors.toList());
  }

  @Override
  public PageResult<EventVo> list(EventFindDto dto) {
    Page<Event> page = eventQuery.list(getSpecification(dto), dto.tranPage());
    List<EventVo> voPage = page.getContent().stream()
        .map(e -> toVo(e, eventUrlPrefix)).collect(Collectors.toList());
    return PageResult.of(page.getTotalElements(), voPage);
  }

  @Override
  public PageResult<EventVo> search(EventSearchDto dto) {
    Page<Event> page = eventSearch.search(EventAssembler.getSearchCriteria(dto),
        dto.tranPage(), Event.class, getMatchSearchFields(dto.getClass()));
    List<EventVo> voPage = page.getContent().stream()
        .map(e -> toVo(e, eventUrlPrefix)).collect(Collectors.toList());
    return PageResult.of(page.getTotalElements(), voPage);
  }

}
