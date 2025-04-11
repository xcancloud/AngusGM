package cloud.xcan.angus.core.gm.interfaces.policy.facade.internal;

import static cloud.xcan.angus.core.utils.CoreUtils.copyProperties;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;

import cloud.xcan.angus.api.commonlink.app.func.AppFunc;
import cloud.xcan.angus.core.gm.application.cmd.policy.AuthPolicyFuncCmd;
import cloud.xcan.angus.core.gm.application.query.policy.AuthPolicyFuncQuery;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.AuthPolicyFuncFacade;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.internal.assembler.AuthPolicyFuncAssembler;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.func.AuthPolicyFuncTreeVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.func.AuthPolicyFuncVo;
import cloud.xcan.angus.spec.utils.TreeUtils;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;


@Component
public class AuthPolicyFuncFacadeImpl implements AuthPolicyFuncFacade {

  @Resource
  private AuthPolicyFuncCmd authPolicyFuncCmd;

  @Resource
  private AuthPolicyFuncQuery authPolicyFuncQuery;

  @Override
  public void add(Long policyId, Set<Long> appFuncIds) {
    authPolicyFuncCmd.add(policyId, appFuncIds);
  }

  @Override
  public void replace(Long policyId, Set<Long> appFuncIds) {
    authPolicyFuncCmd.replace(policyId, appFuncIds);
  }

  @Override
  public void delete(Long policyId, Set<Long> appFuncIds) {
    authPolicyFuncCmd.delete(policyId, appFuncIds);
  }

  @Override
  public List<AuthPolicyFuncVo> list(Long policyId) {
    List<AppFunc> authPolicyFuncs = authPolicyFuncQuery.list(policyId);
    return authPolicyFuncs.stream().map(AuthPolicyFuncAssembler::toPolicyFuncVo)
        .collect(Collectors.toList());
  }

  @Override
  public List<AuthPolicyFuncTreeVo> tree(Long policyId) {
    // NameJoin
    List<AuthPolicyFuncVo> vos = list(policyId);
    return isEmpty(vos) ? null : TreeUtils.toTree(
        vos.stream().map(x -> copyProperties(x, new AuthPolicyFuncTreeVo()))
            .collect(Collectors.toList()), true);
  }

}
