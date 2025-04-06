package cloud.xcan.angus.core.gm.interfaces.policy.facade;

import cloud.xcan.angus.api.gm.dept.vo.DeptListVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.AuthPolicyFindDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.UnAuthPolicyFindDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.org.AuthPolicyDeptFindDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.AuthPolicyVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.PolicyUnauthVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public interface AuthPolicyDeptFacade {

  List<IdKey<Long, Object>> policyDeptAdd(Long policyId, Set<Long> deptIds);

  void policyDeptDelete(Long policyId, Set<Long> ids);

  PageResult<DeptListVo> policyDeptList(Long policyId, AuthPolicyDeptFindDto dto);

  PageResult<DeptListVo> policyUnauthDeptList(Long policyId, AuthPolicyDeptFindDto dto);

  List<IdKey<Long, Object>> deptPolicyAdd(Long deptId, Set<Long> policyIds);

  void deptPolicyDelete(Long deptId, Set<Long> policyIds);

  void deptPolicyDeleteBatch(HashSet<Long> deptIds, HashSet<Long> policyIds);

  PageResult<AuthPolicyVo> deptPolicyList(Long deptId, AuthPolicyFindDto dto);

  PageResult<PolicyUnauthVo> deptUnauthPolicyList(Long deptId, UnAuthPolicyFindDto dto);

}
