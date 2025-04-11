package cloud.xcan.angus.core.gm.interfaces.policy.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.policy.facade.internal.assembler.AuthPolicyGroupAssembler.addToGroupPolicy;
import static cloud.xcan.angus.core.gm.interfaces.policy.facade.internal.assembler.AuthPolicyGroupAssembler.addToPolicyGroup;
import static cloud.xcan.angus.core.gm.interfaces.policy.facade.internal.assembler.AuthPolicyGroupAssembler.getSpecification;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.api.commonlink.AuthOrgType;
import cloud.xcan.angus.api.commonlink.group.Group;
import cloud.xcan.angus.api.gm.group.vo.GroupListVo;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.policy.AuthPolicyGroupCmd;
import cloud.xcan.angus.core.gm.application.query.policy.AuthPolicyGroupQuery;
import cloud.xcan.angus.core.gm.domain.policy.AuthPolicy;
import cloud.xcan.angus.core.gm.domain.policy.org.AuthPolicyOrg;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.AuthPolicyGroupFacade;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.AuthPolicyFindDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.UnAuthPolicyFindDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.org.AuthPolicyGroupFindDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.internal.assembler.AuthPolicyAssembler;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.internal.assembler.AuthPolicyGroupAssembler;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.AuthPolicyVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.PolicyUnauthVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;


@Component
public class AuthPolicyGroupFacadeImpl implements AuthPolicyGroupFacade {

  @Resource
  private AuthPolicyGroupCmd authPolicyGroupCmd;

  @Resource
  private AuthPolicyGroupQuery authPolicyGroupQuery;

  @Override
  public List<IdKey<Long, Object>> policyGroupAdd(Long policyId, Set<Long> groupIds) {
    List<AuthPolicyOrg> policyGroups = addToPolicyGroup(policyId, groupIds);
    return authPolicyGroupCmd.policyGroupAdd(policyId, policyGroups);
  }

  @Override
  public void policyGroupDelete(Long policyId, Set<Long> groupIds) {
    authPolicyGroupCmd.policyGroupDelete(policyId, groupIds);
  }

  @NameJoin
  @Override
  public PageResult<GroupListVo> policyGroupList(Long policyId, AuthPolicyGroupFindDto dto) {
    dto.setPolicyId(policyId);
    Page<Group> page = authPolicyGroupQuery.policyGroupList(
        getSpecification(dto), dto.tranPage());
    return buildVoPageResult(page, AuthPolicyGroupAssembler::toGroupListVo);
  }

  @Override
  public PageResult<GroupListVo> policyUnauthGroupList(Long policyId, AuthPolicyGroupFindDto dto) {
    dto.setPolicyId(policyId);
    Page<Group> page = authPolicyGroupQuery.policyUnauthGroupList(
        getSpecification(dto), dto.tranPage());
    return buildVoPageResult(page, AuthPolicyGroupAssembler::toGroupListVo);
  }

  @Override
  public List<IdKey<Long, Object>> groupPolicyAdd(Long groupId, Set<Long> policyIds) {
    List<AuthPolicyOrg> policyGroups = addToGroupPolicy(groupId, policyIds);
    return authPolicyGroupCmd.groupPolicyAdd(groupId, policyGroups);
  }

  @Override
  public void groupPolicyDelete(Long groupId, Set<Long> policyIds) {
    authPolicyGroupCmd.groupPolicyDelete(groupId, policyIds);
  }

  @Override
  public void groupPolicyDeleteBatch(HashSet<Long> groupIds, HashSet<Long> policyIds) {
    authPolicyGroupCmd.groupPolicyDeleteBatch(groupIds, policyIds);
  }

  @NameJoin
  @Override
  public PageResult<AuthPolicyVo> groupPolicyList(Long groupId, AuthPolicyFindDto dto) {
    dto.setOrgId(groupId).setOrgType(AuthOrgType.GROUP);
    Page<AuthPolicy> page = authPolicyGroupQuery.groupPolicyList(
        AuthPolicyAssembler.getSpecification(dto), dto.tranPage());
    return buildVoPageResult(page, AuthPolicyAssembler::toAuthPolicyVo);
  }

  @Override
  public PageResult<PolicyUnauthVo> groupUnauthPolicyList(Long groupId, UnAuthPolicyFindDto dto) {
    dto.setOrgId(groupId).setOrgType(AuthOrgType.GROUP).setIgnoreAuthOrg(true);
    Page<AuthPolicy> page = authPolicyGroupQuery.groupUnauthPolicyList(
        AuthPolicyAssembler.getSpecification(dto), dto.tranPage());
    return buildVoPageResult(page, AuthPolicyAssembler::toUnAuthPolicyVo);
  }
}
