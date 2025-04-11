package cloud.xcan.angus.core.gm.interfaces.policy.facade;

import cloud.xcan.angus.api.gm.user.vo.UserListVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.AuthPolicyAssociatedFindDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.AuthPolicyFindDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.UnAuthPolicyAssociatedFindDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.org.AuthPolicyUserFindDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.AuthPolicyAssociatedVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.AuthPolicyVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.PolicyUnauthVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface AuthPolicyUserFacade {

  List<IdKey<Long, Object>> policyUserAdd(Long policyId, Set<Long> userIds);

  void policyUserDelete(Long policyId, Set<Long> userIds);

  PageResult<UserListVo> policyUserList(Long policyId, AuthPolicyUserFindDto dto);

  PageResult<UserListVo> policyUnauthUserList(Long policyId, AuthPolicyUserFindDto dto);

  List<IdKey<Long, Object>> userPolicyAdd(Long userId, Set<Long> policyIds);

  void userPolicyDelete(Long userId, Set<Long> policyIds);

  void userPolicyDeleteBatch(HashSet<Long> userIds, HashSet<Long> policyIds);

  PageResult<AuthPolicyVo> userPolicyList(Long userId, AuthPolicyFindDto dto);

  PageResult<AuthPolicyAssociatedVo> userAssociatedPolicyList(Long userId,
      AuthPolicyAssociatedFindDto dto);

  PageResult<PolicyUnauthVo> userUnauthPolicyList(Long userId, UnAuthPolicyAssociatedFindDto dto);

}
