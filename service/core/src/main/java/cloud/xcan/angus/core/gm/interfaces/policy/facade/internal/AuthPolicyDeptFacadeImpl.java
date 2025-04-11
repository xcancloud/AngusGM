package cloud.xcan.angus.core.gm.interfaces.policy.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.policy.facade.internal.assembler.AuthPolicyDeptAssembler.addToDeptPolicy;
import static cloud.xcan.angus.core.gm.interfaces.policy.facade.internal.assembler.AuthPolicyDeptAssembler.addToPolicyDept;
import static cloud.xcan.angus.core.gm.interfaces.policy.facade.internal.assembler.AuthPolicyDeptAssembler.getSpecification;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.api.commonlink.AuthOrgType;
import cloud.xcan.angus.api.commonlink.dept.Dept;
import cloud.xcan.angus.api.gm.dept.vo.DeptListVo;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.policy.AuthPolicyDeptCmd;
import cloud.xcan.angus.core.gm.application.query.policy.AuthPolicyDeptQuery;
import cloud.xcan.angus.core.gm.domain.policy.AuthPolicy;
import cloud.xcan.angus.core.gm.domain.policy.org.AuthPolicyOrg;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.AuthPolicyDeptFacade;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.AuthPolicyFindDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.UnAuthPolicyFindDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.org.AuthPolicyDeptFindDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.internal.assembler.AuthPolicyAssembler;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.internal.assembler.AuthPolicyDeptAssembler;
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
public class AuthPolicyDeptFacadeImpl implements AuthPolicyDeptFacade {

  @Resource
  private AuthPolicyDeptCmd authPolicyDeptCmd;

  @Resource
  private AuthPolicyDeptQuery authPolicyDeptQuery;

  @Override
  public List<IdKey<Long, Object>> policyDeptAdd(Long policyId, Set<Long> deptIds) {
    List<AuthPolicyOrg> policyDepartments = addToPolicyDept(policyId, deptIds);
    return authPolicyDeptCmd.policyDeptAdd(policyId, policyDepartments);
  }

  @Override
  public void policyDeptDelete(Long policyId, Set<Long> deptIds) {
    authPolicyDeptCmd.policyDeptDelete(policyId, deptIds);
  }

  @NameJoin
  @Override
  public PageResult<DeptListVo> policyDeptList(Long policyId, AuthPolicyDeptFindDto dto) {
    dto.setPolicyId(policyId);
    Page<Dept> page = authPolicyDeptQuery.policyDeptList(
        getSpecification(dto), dto.tranPage());
    return buildVoPageResult(page, AuthPolicyDeptAssembler::toDeptListVo);
  }

  @Override
  public PageResult<DeptListVo> policyUnauthDeptList(Long policyId, AuthPolicyDeptFindDto dto) {
    dto.setPolicyId(policyId);
    Page<Dept> page = authPolicyDeptQuery.policyUnauthDeptList(
        getSpecification(dto), dto.tranPage());
    return buildVoPageResult(page, AuthPolicyDeptAssembler::toDeptListVo);
  }

  @Override
  public List<IdKey<Long, Object>> deptPolicyAdd(Long deptId, Set<Long> policyIds) {
    List<AuthPolicyOrg> policyDepartments = addToDeptPolicy(deptId, policyIds);
    return authPolicyDeptCmd.deptPolicyAdd(deptId, policyDepartments);
  }

  @Override
  public void deptPolicyDelete(Long deptId, Set<Long> policyIds) {
    authPolicyDeptCmd.deptPolicyDelete(deptId, policyIds);
  }

  @Override
  public void deptPolicyDeleteBatch(HashSet<Long> deptIds, HashSet<Long> policyIds) {
    authPolicyDeptCmd.deptPolicyDeleteBatch(deptIds, policyIds);
  }

  @NameJoin
  @Override
  public PageResult<AuthPolicyVo> deptPolicyList(Long deptId, AuthPolicyFindDto dto) {
    dto.setOrgId(deptId).setOrgType(AuthOrgType.DEPT);
    Page<AuthPolicy> page = authPolicyDeptQuery.deptPolicyList(
        AuthPolicyAssembler.getSpecification(dto), dto.tranPage());
    return buildVoPageResult(page, AuthPolicyAssembler::toAuthPolicyVo);
  }

  @NameJoin
  @Override
  public PageResult<PolicyUnauthVo> deptUnauthPolicyList(Long deptId, UnAuthPolicyFindDto dto) {
    dto.setOrgId(deptId).setOrgType(AuthOrgType.DEPT).setIgnoreAuthOrg(true);
    Page<AuthPolicy> page = authPolicyDeptQuery.deptUnauthPolicyList(
        AuthPolicyAssembler.getSpecification(dto), dto.tranPage());
    return buildVoPageResult(page, AuthPolicyAssembler::toUnAuthPolicyVo);
  }

}
