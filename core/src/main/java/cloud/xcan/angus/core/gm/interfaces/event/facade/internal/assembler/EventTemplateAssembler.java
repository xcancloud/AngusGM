package cloud.xcan.angus.core.gm.interfaces.event.facade.internal.assembler;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.user.UserBase;
import cloud.xcan.angus.api.pojo.UserName;
import cloud.xcan.angus.core.gm.domain.email.template.EventTemplate;
import cloud.xcan.angus.core.gm.domain.event.ReceiveChannelType;
import cloud.xcan.angus.core.gm.domain.event.template.receiver.EventTemplateReceiver;
import cloud.xcan.angus.core.gm.interfaces.event.facade.dto.template.EventTemplateAddDto;
import cloud.xcan.angus.core.gm.interfaces.event.facade.dto.template.EventTemplateFindDto;
import cloud.xcan.angus.core.gm.interfaces.event.facade.dto.template.EventTemplateReceiverDto;
import cloud.xcan.angus.core.gm.interfaces.event.facade.dto.template.EventTemplateReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.event.facade.dto.template.EventTemplateSearchDto;
import cloud.xcan.angus.core.gm.interfaces.event.facade.vo.channel.EventChannelInfoVo;
import cloud.xcan.angus.core.gm.interfaces.event.facade.vo.template.EventTemplateCurrentDetailVo;
import cloud.xcan.angus.core.gm.interfaces.event.facade.vo.template.EventTemplateReceiveVo;
import cloud.xcan.angus.core.gm.interfaces.event.facade.vo.template.EventTemplateReceiverInfoVo;
import cloud.xcan.angus.core.gm.interfaces.event.facade.vo.template.EventTemplateVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.jpa.domain.Specification;


public class EventTemplateAssembler {

  public static EventTemplate toAddDomain(EventTemplateAddDto dto) {
    return new EventTemplate()
        .setAppCode(dto.getAppCode())
        .setEventCode(dto.getEventCode())
        .setEventName(dto.getEventName())
        .setEventType(dto.getEventType())
        .setTargetType(dto.getTargetType())
        .setPrivate0(dto.getPrivate0())
        .setAllowedChannelTypes(dto.getAllowedChannelTypes());
  }

  public static EventTemplate toReplaceDomain(EventTemplateReplaceDto dto) {
    return new EventTemplate().setId(dto.getId())
        .setAppCode(dto.getAppCode())
        .setEventCode(dto.getEventCode())
        .setEventName(dto.getEventName())
        .setEventType(dto.getEventType())
        .setTargetType(dto.getTargetType())
        .setPrivate0(dto.getPrivate0())
        .setAllowedChannelTypes(dto.getAllowedChannelTypes());
  }

  public static EventTemplateReceiver toTemplateReceiver(EventTemplateReceiverDto dto) {
    return new EventTemplateReceiver().setTemplateId(dto.getId())
        .setReceiverTypes(dto.getReceiverTypes())
        .setReceiverIds(dto.getReceiverIds())
        .setNoticeTypes(dto.getNoticeTypes());
  }

  public static EventTemplateVo toVo(EventTemplate template) {
    return new EventTemplateVo().setId(template.getId())
        .setAppCode(template.getAppCode())
        .setEventCode(template.getEventCode())
        .setEventName(template.getEventName())
        .setEventType(template.getEventType())
        .setTargetType(template.getTargetType())
        .setPrivate0(template.getPrivate0())
        .setAllowedChannelTypes(template.getAllowedChannelTypes());
  }

  public static EventTemplateCurrentDetailVo toCurrentVo(EventTemplate template,
      List<UserBase> users) {
    EventTemplateCurrentDetailVo vo = new EventTemplateCurrentDetailVo()
        .setId(template.getId())
        .setAppCode(template.getAppCode())
        .setEventCode(template.getEventCode())
        .setEventName(template.getEventName())
        .setEventType(template.getEventType())
        .setEKey(template.getEKey())
        .setTargetType(template.getTargetType())
        .setPrivate0(template.getPrivate0())
        .setAllowedChannelTypes(template.getAllowedChannelTypes());

    EventTemplateReceiveVo receiveSetting = new EventTemplateReceiveVo();
    if (nonNull(template.getReceivers())) {
      EventTemplateReceiverInfoVo receivers = new EventTemplateReceiverInfoVo();
      receivers.setId(template.getReceivers().getId())
          .setReceiverTypes(template.getReceivers().getReceiverTypes())
          .setReceivers(isEmpty(users) ? null : users.stream()
              .map(x -> new UserName().setId(x.getId()).setFullName(x.getFullName()))
              .collect(Collectors.toSet()))
          .setNoticeTypes(template.getReceivers().getNoticeTypes());
      receiveSetting.setReceivers(receivers);
    }

    if (isNotEmpty(template.getChannels())) {
      receiveSetting.setChannels(
          template.getChannels().stream().map(x ->
                  new EventChannelInfoVo().setId(x.getId())
                      .setChannelType(ReceiveChannelType.valueOf(x.getType()))
                      .setName(x.getName()).setAddress(x.getAddress()))
              .collect(Collectors.toList()));
    }
    vo.setReceiveSetting(receiveSetting);
    return vo;
  }

  public static Specification<EventTemplate> getSpecification(EventTemplateFindDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .matchSearchFields("eventName", "eventCode", "eKey")
        .rangeSearchFields("id", "createdDate")
        .orderByFields("id", "createdDate")
        .build();
    return new GenericSpecification<>(filters);
  }

  public static Set<SearchCriteria> getSearchCriteria(EventTemplateSearchDto dto) {
    // Build the final filters
    return new SearchCriteriaBuilder<>(dto)
        .matchSearchFields("eventName", "eventCode", "eKey")
        .rangeSearchFields("id", "createdDate")
        .orderByFields("id", "createdDate")
        .build();
  }
}
