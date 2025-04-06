package cloud.xcan.angus.core.gm.interfaces.policy.facade;

import cloud.xcan.angus.api.gm.group.vo.GroupListVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.AuthPolicyFindDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.UnAuthPolicyFindDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.org.AuthPolicyGroupFindDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.AuthPolicyVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.PolicyUnauthVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public interface AuthPolicyGroupFacade {

  List<IdKey<Long, Object>> policyGroupAdd(Long policyId, Set<Long> groupIds);

  void policyGroupDelete(Long policyId, Set<Long> ids);

  PageResult<GroupListVo> policyGroupList(Long policyId, AuthPolicyGroupFindDto dto);

  PageResult<GroupListVo> policyUnauthGroupList(Long policyId, AuthPolicyGroupFindDto dto);

  List<IdKey<Long, Object>> groupPolicyAdd(Long groupId, Set<Long> policyIds);

  void groupPolicyDelete(Long groupId, Set<Long> policyIds);

  void groupPolicyDeleteBatch(HashSet<Long> groupIds, HashSet<Long> policyIds);

  PageResult<AuthPolicyVo> groupPolicyList(Long groupId, AuthPolicyFindDto dto);

  PageResult<PolicyUnauthVo> groupUnauthPolicyList(Long groupId, UnAuthPolicyFindDto dto);

}
