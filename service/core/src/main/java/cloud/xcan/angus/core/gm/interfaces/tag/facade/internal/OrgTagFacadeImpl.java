package cloud.xcan.angus.core.gm.interfaces.tag.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.tag.facade.internal.assembler.OrgTagAssembler.domainToDetailVo;
import static cloud.xcan.angus.core.gm.interfaces.tag.facade.internal.assembler.OrgTagAssembler.getSpecification;
import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.api.commonlink.tag.OrgTag;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.tag.OrgTagCmd;
import cloud.xcan.angus.core.gm.application.query.tag.OrgTagQuery;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.OrgTagFacade;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.OrgTagAddDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.OrgTagFindDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.OrgTagUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.internal.assembler.OrgTagAssembler;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.OrgTagDetailVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;


@Component
public class OrgTagFacadeImpl implements OrgTagFacade {

  @Resource
  private OrgTagCmd orgTagCmd;

  @Resource
  private OrgTagQuery orgTagQuery;

  @Override
  public List<IdKey<Long, Object>> add(List<OrgTagAddDto> dto) {
    return orgTagCmd.add(dto.stream().map(OrgTagAssembler::addDtoToDomain)
        .collect(Collectors.toList()));
  }

  @Override
  public void update(List<OrgTagUpdateDto> dto) {
    orgTagCmd.update(dto.stream().map(OrgTagAssembler::updateDtoToDomain)
        .collect(Collectors.toList()));
  }

  @Override
  public void delete(HashSet<Long> ids) {
    orgTagCmd.delete(ids);
  }

  @NameJoin
  @Override
  public OrgTagDetailVo detail(Long id) {
    return domainToDetailVo(orgTagQuery.detail(id));
  }

  @NameJoin
  @Override
  public PageResult<OrgTagDetailVo> list(OrgTagFindDto dto) {
    Page<OrgTag> page = orgTagQuery.list(getSpecification(dto), dto.tranPage(),
        dto.fullTextSearch, getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, OrgTagAssembler::domainToDetailVo);
  }

}
