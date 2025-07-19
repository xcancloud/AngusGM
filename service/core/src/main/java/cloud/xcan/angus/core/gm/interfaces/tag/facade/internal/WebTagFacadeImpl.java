package cloud.xcan.angus.core.gm.interfaces.tag.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.tag.facade.internal.assembler.WebTagAssembler.domainToDetailVo;
import static cloud.xcan.angus.core.gm.interfaces.tag.facade.internal.assembler.WebTagAssembler.getSpecification;
import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.api.commonlink.app.tag.WebTag;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.tag.WebTagCmd;
import cloud.xcan.angus.core.gm.application.query.tag.WebTagQuery;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.WebTagFacade;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.WebTagAddDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.WebTagFindDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.WebTagUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.internal.assembler.WebTagAssembler;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.WebTagDetailVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;


@Component
public class WebTagFacadeImpl implements WebTagFacade {

  @Resource
  private WebTagCmd webTagCmd;

  @Resource
  private WebTagQuery webTagQuery;

  @Override
  public List<IdKey<Long, Object>> add(List<WebTagAddDto> dto) {
    return webTagCmd.add(dto.stream().map(WebTagAssembler::addDtoToDomain)
        .collect(Collectors.toList()));
  }

  @Override
  public void update(List<WebTagUpdateDto> dto) {
    webTagCmd.update(dto.stream().map(WebTagAssembler::updateDtoToDomain)
        .collect(Collectors.toList()));
  }

  @Override
  public void delete(HashSet<Long> ids) {
    webTagCmd.delete(ids);
  }

  @NameJoin
  @Override
  public WebTagDetailVo detail(Long id) {
    return domainToDetailVo(webTagQuery.detail(id));
  }

  @NameJoin
  @Override
  public PageResult<WebTagDetailVo> list(WebTagFindDto dto) {
    Page<WebTag> page = webTagQuery.list(getSpecification(dto), dto.tranPage(),
        dto.fullTextSearch, getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, WebTagAssembler::domainToDetailVo);
  }

}
