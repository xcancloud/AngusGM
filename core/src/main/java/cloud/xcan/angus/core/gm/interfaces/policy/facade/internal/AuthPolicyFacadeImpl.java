package cloud.xcan.angus.core.gm.interfaces.policy.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.policy.facade.internal.assembler.AuthPolicyAssembler.toDetailVo;
import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.policy.AuthPolicyCmd;
import cloud.xcan.angus.core.gm.application.query.policy.AuthPolicyQuery;
import cloud.xcan.angus.core.gm.application.query.policy.AuthPolicySearch;
import cloud.xcan.angus.core.gm.domain.policy.AuthPolicy;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.AuthPolicyFacade;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.AuthPolicyAddDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.AuthPolicyFindDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.AuthPolicyInitDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.AuthPolicyReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.AuthPolicySearchDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.AuthPolicyUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.internal.assembler.AuthPolicyAssembler;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.AuthPolicyDetailVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.AuthPolicyVo;
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
public class AuthPolicyFacadeImpl implements AuthPolicyFacade {

  @Resource
  private AuthPolicyCmd authPolicyCmd;

  @Resource
  private AuthPolicyQuery authPolicyQuery;

  @Resource
  private AuthPolicySearch authPolicySearch;

  @Override
  public List<IdKey<Long, Object>> add(List<AuthPolicyAddDto> dto) {
    return authPolicyCmd.add(dto.stream().map(AuthPolicyAssembler::addDtoToDomain)
        .collect(Collectors.toList()));
  }

  @Override
  public void update(List<AuthPolicyUpdateDto> dto) {
    authPolicyCmd.update(dto.stream().map(AuthPolicyAssembler::updateDtoToDomain)
        .collect(Collectors.toList()));
  }

  @Override
  public List<IdKey<Long, Object>> replace(List<AuthPolicyReplaceDto> dto) {
    return authPolicyCmd.replace(dto.stream().map(AuthPolicyAssembler::replaceDtoToDomain)
        .collect(Collectors.toList()));
  }

  @Override
  public void enabled(List<EnabledOrDisabledDto> dto) {
    authPolicyCmd.enabled(dto.stream().map(AuthPolicyAssembler::enabledDtoToDomain)
        .collect(Collectors.toList()));
  }

  @Override
  public void init(AuthPolicyInitDto dto) {
    authPolicyCmd.initAndOpenAppByPolicy(dto.getId());
  }

  @Override
  public void delete(HashSet<Long> ids) {
    authPolicyCmd.delete(ids);
  }

  @NameJoin
  @Override
  public AuthPolicyDetailVo detail(String idOrCode) {
    return toDetailVo(authPolicyQuery.detail(idOrCode));
  }

  @NameJoin
  @Override
  public PageResult<AuthPolicyVo> list(AuthPolicyFindDto dto) {
    Page<AuthPolicy> page = authPolicyQuery.list(AuthPolicyAssembler.getSpecification(dto),
        dto.tranPage());
    return buildVoPageResult(page, AuthPolicyAssembler::toAuthPolicyVo);
  }

  @NameJoin
  @Override
  public PageResult<AuthPolicyVo> search(AuthPolicySearchDto dto) {
    Page<AuthPolicy> page = authPolicySearch.search(AuthPolicyAssembler.getCriteria(dto),
        dto.tranPage(),
        getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, AuthPolicyAssembler::toAuthPolicyVo);
  }

}
