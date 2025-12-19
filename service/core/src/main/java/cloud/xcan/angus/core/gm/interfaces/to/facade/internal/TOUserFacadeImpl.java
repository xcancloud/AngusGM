package cloud.xcan.angus.core.gm.interfaces.to.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.to.facade.internal.assembler.TOUserAssembler.getSpecification;
import static cloud.xcan.angus.core.gm.interfaces.to.facade.internal.assembler.TOUserAssembler.toDetailVo;
import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.api.commonlink.to.TOUser;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.to.TOUserCmd;
import cloud.xcan.angus.core.gm.application.query.to.TOUserQuery;
import cloud.xcan.angus.core.gm.interfaces.to.facade.TOUserFacade;
import cloud.xcan.angus.core.gm.interfaces.to.facade.dto.TOUserAddDto;
import cloud.xcan.angus.core.gm.interfaces.to.facade.dto.TOUserFindDto;
import cloud.xcan.angus.core.gm.interfaces.to.facade.internal.assembler.TOUserAssembler;
import cloud.xcan.angus.core.gm.interfaces.to.facade.vo.TOUserDetailVo;
import cloud.xcan.angus.core.gm.interfaces.to.facade.vo.TOUserVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;


@Component
public class TOUserFacadeImpl implements TOUserFacade {

  @Resource
  private TOUserCmd toUserCmd;

  @Resource
  private TOUserQuery toUserQuery;

  @Override
  public List<IdKey<Long, Object>> add(List<TOUserAddDto> dto) {
    List<TOUser> toUsers = dto.stream().map(TOUserAssembler::addDtoToDomain)
        .collect(Collectors.toList());
    return toUserCmd.add(toUsers);
  }

  @Override
  public void delete(HashSet<Long> ids) {
    toUserCmd.delete(ids);
  }

  @NameJoin
  @Override
  public TOUserDetailVo detail(Long userId) {
    return toDetailVo(toUserQuery.detail(userId));
  }

  @NameJoin
  @Override
  public PageResult<TOUserVo> list(TOUserFindDto dto) {
    Page<TOUser> page = toUserQuery.list(getSpecification(dto), dto.tranPage()
        , dto.fullTextSearch, getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, TOUserAssembler::toVo);
  }
}
