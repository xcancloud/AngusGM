package cloud.xcan.angus.core.gm.interfaces.policy.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.policy.facade.internal.assembler.AuthPolicyTenantAssembler.toDefaultPolicyVo;
import static cloud.xcan.angus.core.utils.CoreUtils.copyProperties;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;

import cloud.xcan.angus.api.commonlink.app.func.AppFunc;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.policy.AuthPolicyTenantCmd;
import cloud.xcan.angus.core.gm.application.query.policy.AuthPolicyTenantQuery;
import cloud.xcan.angus.core.gm.domain.app.App;
import cloud.xcan.angus.core.gm.domain.policy.org.AuthPolicyOrg;
import cloud.xcan.angus.core.gm.interfaces.app.facade.internal.assembler.AppAssembler;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.AppVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.AuthPolicyTenantFacade;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.internal.assembler.AuthPolicyFuncAssembler;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.func.AuthPolicyFuncTreeVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.func.AuthPolicyFuncVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.org.AuthAppDefaultPolicyVo;
import cloud.xcan.angus.spec.utils.TreeUtils;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;


@Component
public class AuthPolicyTenantFacadeImpl implements AuthPolicyTenantFacade {

  @Resource
  private AuthPolicyTenantCmd authPolicyTenantCmd;

  @Resource
  private AuthPolicyTenantQuery authPolicyTenantQuery;

  @Override
  public void defaultPolicySet(Long appId, Long policyId) {
    authPolicyTenantCmd.defaultPolicySet(appId, policyId);
  }

  @Override
  public void defaultPolicyDelete(Long appId) {
    authPolicyTenantCmd.defaultPolicyDelete(appId);
  }

  @Override
  public List<AuthAppDefaultPolicyVo> defaultPolicy() {
    List<AuthPolicyOrg> authTenantPolicies = authPolicyTenantQuery.defaultPolicy();
    return toDefaultPolicyVo(authTenantPolicies);
  }

  @Override
  public List<AppVo> tenantAppList() {
    List<App> apps = authPolicyTenantQuery.tenantAppList();
    return apps.stream().map(AppAssembler::toVo).collect(Collectors.toList());
  }

  @NameJoin
  @Override
  public List<AuthPolicyFuncVo> tenantAppFuncList(Long appId) {
    List<AppFunc> appFunctions = authPolicyTenantQuery.tenantAppFuncList(appId);
    return appFunctions.stream().map(AuthPolicyFuncAssembler::toPolicyFuncVo)
        .collect(Collectors.toList());
  }

  @Override
  public List<AuthPolicyFuncTreeVo> tenantAppFuncTree(Long appId) {
    // NameJoin
    List<AuthPolicyFuncVo> vos = tenantAppFuncList(appId);
    return isEmpty(vos) ? null : TreeUtils.toTree(
        vos.stream().map(x -> copyProperties(x, new AuthPolicyFuncTreeVo()))
            .collect(Collectors.toList()), true);
  }
}
