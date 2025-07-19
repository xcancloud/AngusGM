package cloud.xcan.angus.core.gm.interfaces.event.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.event.facade.internal.assembler.EventTemplateAssembler.getSpecification;
import static cloud.xcan.angus.core.gm.interfaces.event.facade.internal.assembler.EventTemplateAssembler.toAddDomain;
import static cloud.xcan.angus.core.gm.interfaces.event.facade.internal.assembler.EventTemplateAssembler.toCurrentVo;
import static cloud.xcan.angus.core.gm.interfaces.event.facade.internal.assembler.EventTemplateAssembler.toReplaceDomain;
import static cloud.xcan.angus.core.gm.interfaces.event.facade.internal.assembler.EventTemplateAssembler.toTemplateReceiver;
import static cloud.xcan.angus.core.gm.interfaces.event.facade.internal.assembler.EventTemplateAssembler.toVo;
import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.api.commonlink.user.UserBase;
import cloud.xcan.angus.api.manager.UserManager;
import cloud.xcan.angus.core.gm.application.cmd.event.EventTemplateChannelCmd;
import cloud.xcan.angus.core.gm.application.cmd.event.EventTemplateCmd;
import cloud.xcan.angus.core.gm.application.cmd.event.EventTemplateReceiverCmd;
import cloud.xcan.angus.core.gm.application.query.event.EventTemplateQuery;
import cloud.xcan.angus.core.gm.domain.email.template.EventTemplate;
import cloud.xcan.angus.core.gm.interfaces.event.facade.EventTemplateFacade;
import cloud.xcan.angus.core.gm.interfaces.event.facade.dto.template.EventTemplateAddDto;
import cloud.xcan.angus.core.gm.interfaces.event.facade.dto.template.EventTemplateChannelReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.event.facade.dto.template.EventTemplateFindDto;
import cloud.xcan.angus.core.gm.interfaces.event.facade.dto.template.EventTemplateReceiverDto;
import cloud.xcan.angus.core.gm.interfaces.event.facade.dto.template.EventTemplateReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.event.facade.internal.assembler.EventTemplateAssembler;
import cloud.xcan.angus.core.gm.interfaces.event.facade.vo.template.EventTemplateCurrentDetailVo;
import cloud.xcan.angus.core.gm.interfaces.event.facade.vo.template.EventTemplateVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;


@Component
public class EventTemplateFacadeImpl implements EventTemplateFacade {

  @Resource
  private EventTemplateQuery eventTemplateQuery;

  @Resource
  private EventTemplateCmd eventTemplateCmd;

  @Resource
  private EventTemplateChannelCmd eventTemplateChannelCmd;

  @Resource
  private EventTemplateReceiverCmd eventTemplateReceiverCmd;

  @Resource
  private UserManager userManager;

  @Override
  public IdKey<Long, Object> add(EventTemplateAddDto dto) {
    return eventTemplateCmd.add(toAddDomain(dto));
  }

  @Override
  public IdKey<Long, Object> replace(EventTemplateReplaceDto dto) {
    return eventTemplateCmd.replace(toReplaceDomain(dto));
  }

  @Override
  public void channelReplace(EventTemplateChannelReplaceDto dto) {
    eventTemplateChannelCmd.channelReplace(dto.getId(), dto.getChannelIds());
  }

  @Override
  public void receiverReplace(EventTemplateReceiverDto dto) {
    eventTemplateReceiverCmd.receiverReplace(toTemplateReceiver(dto));
  }

  @Override
  public void delete(Long id) {
    eventTemplateCmd.delete(id);
  }

  @Override
  public EventTemplateVo detail(Long id) {
    EventTemplate template = eventTemplateQuery.detail(id, false);
    return toVo(template);
  }

  @Override
  public EventTemplateCurrentDetailVo currentDetail(Long id) {
    EventTemplate template = eventTemplateQuery.detail(id, true);
    List<UserBase> users = template.hasReceiverIds() ? userManager
        .findUserBases(template.getReceivers().getReceiverIds()) : null;
    return toCurrentVo(template, users);
  }

  @Override
  public PageResult<EventTemplateVo> list(EventTemplateFindDto dto) {
    Page<EventTemplate> page = eventTemplateQuery.list(getSpecification(dto), false,
        dto.tranPage(), dto.fullTextSearch, getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, EventTemplateAssembler::toVo);
  }

  @Override
  public PageResult<EventTemplateCurrentDetailVo> currentList(EventTemplateFindDto dto) {
    Page<EventTemplate> page = eventTemplateQuery.list(getSpecification(dto), true,
        dto.tranPage(), dto.fullTextSearch, getMatchSearchFields(dto.getClass()));
    if (page.isEmpty()) {
      return PageResult.empty();
    }
    List<EventTemplateCurrentDetailVo> vos = getEventTemplateCurrentDetailVos(page);
    return PageResult.of(page.getTotalElements(), vos);
  }

  private List<EventTemplateCurrentDetailVo> getEventTemplateCurrentDetailVos(
      Page<EventTemplate> page) {
    List<EventTemplateCurrentDetailVo> vos = new ArrayList<>();
    for (EventTemplate template : page.getContent()) {
      List<UserBase> users = template.hasReceiverIds()
          ? userManager.findUserBases(template.getReceivers().getReceiverIds()) : null;
      vos.add(toCurrentVo(template, users));
    }
    return vos;
  }
}
