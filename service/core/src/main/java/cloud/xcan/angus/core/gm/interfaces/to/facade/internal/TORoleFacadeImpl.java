package cloud.xcan.angus.core.gm.interfaces.to.facade.internal;

import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.api.commonlink.to.TORole;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.to.TORoleCmd;
import cloud.xcan.angus.core.gm.application.query.to.TORoleQuery;
import cloud.xcan.angus.core.gm.application.query.to.TORoleSearch;
import cloud.xcan.angus.core.gm.interfaces.to.facade.TORoleFacade;
import cloud.xcan.angus.core.gm.interfaces.to.facade.dto.TORoleAddDto;
import cloud.xcan.angus.core.gm.interfaces.to.facade.dto.TORoleFindDto;
import cloud.xcan.angus.core.gm.interfaces.to.facade.dto.TORoleReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.to.facade.dto.TORoleSearchDto;
import cloud.xcan.angus.core.gm.interfaces.to.facade.dto.TORoleUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.to.facade.internal.assembler.TORoleAssembler;
import cloud.xcan.angus.core.gm.interfaces.to.facade.vo.TORoleDetailVo;
import cloud.xcan.angus.core.gm.interfaces.to.facade.vo.TORoleVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.remote.dto.EnabledOrDisabledDto;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;


@Component
public class TORoleFacadeImpl implements TORoleFacade {

  @Resource
  private TORoleCmd toRoleCmd;

  @Resource
  private TORoleQuery toRoleQuery;

  @Resource
  private TORoleSearch toRoleSearch;

  @Override
  public List<IdKey<Long, Object>> add(List<TORoleAddDto> dto) {
    return toRoleCmd.add(dto.stream().map(TORoleAssembler::dtoToRole)
        .collect(Collectors.toList()));
  }

  @Override
  public void update(List<TORoleUpdateDto> dto) {
    toRoleCmd.update(dto.stream().map(TORoleAssembler::updateToRole)
        .collect(Collectors.toList()));
  }

  @Override
  public List<IdKey<Long, Object>> replace(List<TORoleReplaceDto> dto) {
    return toRoleCmd.replace(dto.stream().map(TORoleAssembler::replaceToRole)
        .collect(Collectors.toList()));
  }

  @Override
  public void delete(HashSet<Long> ids) {
    toRoleCmd.delete(ids);
  }

  @Override
  public void enabled(List<EnabledOrDisabledDto> dto) {
    toRoleCmd.enabled(dto.stream().map(TORoleAssembler::enabledToRole)
        .collect(Collectors.toList()));
  }

  @NameJoin
  @Override
  public TORoleDetailVo detail(String idOrCode) {
    return TORoleAssembler.toTORoleDetailVo(toRoleQuery.detail(idOrCode));
  }

  @NameJoin
  @Override
  public PageResult<TORoleVo> list(TORoleFindDto dto) {
    Page<TORole> topPolicyPage = toRoleQuery
        .list(TORoleAssembler.getSpecification(dto), dto.tranPage());
    return buildVoPageResult(topPolicyPage, TORoleAssembler::toRoleVo);
  }

  @NameJoin
  @Override
  public PageResult<TORoleVo> search(TORoleSearchDto dto) {
    Page<TORole> topPolicyPage = toRoleSearch
        .search(TORoleAssembler.getSearchCriteria(dto), dto.tranPage(),
            TORole.class, getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(topPolicyPage, TORoleAssembler::toRoleVo);
  }
}
