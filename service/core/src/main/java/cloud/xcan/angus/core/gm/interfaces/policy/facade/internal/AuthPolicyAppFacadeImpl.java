package cloud.xcan.angus.core.gm.interfaces.policy.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.app.facade.internal.assembler.AppAssembler.toDetailVo;
import static cloud.xcan.angus.core.gm.interfaces.policy.facade.internal.assembler.AuthPolicyAppAssembler.getSpecification;
import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.query.app.AppQuery;
import cloud.xcan.angus.core.gm.application.query.policy.AuthPolicyQuery;
import cloud.xcan.angus.core.gm.domain.policy.AuthPolicy;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.AppDetailVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.AuthPolicyAppFacade;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.app.AppPolicyFindDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.internal.assembler.AuthPolicyAppAssembler;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.app.AppPolicyVo;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class AuthPolicyAppFacadeImpl implements AuthPolicyAppFacade {

  @Resource
  private AuthPolicyQuery authPolicyQuery;

  @Resource
  private AppQuery appQuery;

  @NameJoin
  @Override
  public PageResult<AppPolicyVo> appPolicyList(Long appId, AppPolicyFindDto dto) {
    dto.setAppId(appId);
    Page<AuthPolicy> page = authPolicyQuery.list(getSpecification(dto), dto.tranPage(),
        dto.fullTextSearch, getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, AuthPolicyAppAssembler::toPolicyListVo);
  }

  @NameJoin
  @Override
  public AppDetailVo policyAppDetail(Long policyId) {
    AuthPolicy authPolicy = authPolicyQuery.detail(String.valueOf(policyId));
    return toDetailVo(appQuery.detail(authPolicy.getAppId()));
  }
}
