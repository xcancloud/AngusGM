package cloud.xcan.angus.core.gm.interfaces.policy.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.policy.facade.internal.assembler.AuthPolicyUserAssembler.addToPolicyUser;
import static cloud.xcan.angus.core.gm.interfaces.policy.facade.internal.assembler.AuthPolicyUserAssembler.addToUserPolicy;
import static cloud.xcan.angus.core.gm.interfaces.policy.facade.internal.assembler.AuthPolicyUserAssembler.getSpecification;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.AuthOrgType;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.gm.user.vo.UserListVo;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.policy.AuthPolicyUserCmd;
import cloud.xcan.angus.core.gm.application.query.policy.AuthPolicyUserQuery;
import cloud.xcan.angus.core.gm.domain.policy.AuthPolicy;
import cloud.xcan.angus.core.gm.domain.policy.org.AuthPolicyOrg;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.AuthPolicyUserFacade;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.AuthPolicyAssociatedFindDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.AuthPolicyFindDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.UnAuthPolicyAssociatedFindDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.org.AuthPolicyUserFindDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.internal.assembler.AuthPolicyAssembler;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.internal.assembler.AuthPolicyUserAssembler;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.AuthPolicyAssociatedVo;
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
public class AuthPolicyUserFacadeImpl implements AuthPolicyUserFacade {

  @Resource
  private AuthPolicyUserCmd authPolicyUserCmd;

  @Resource
  private AuthPolicyUserQuery authPolicyUserQuery;

  @Override
  public List<IdKey<Long, Object>> policyUserAdd(Long policyId, Set<Long> userIds) {
    List<AuthPolicyOrg> policyUsers = addToPolicyUser(policyId, userIds);
    return authPolicyUserCmd.policyUserAdd(policyId, policyUsers);
  }

  @Override
  public void policyUserDelete(Long policyId, Set<Long> userIds) {
    authPolicyUserCmd.policyUserDelete(policyId, userIds);
  }

  @NameJoin
  @Override
  public PageResult<UserListVo> policyUserList(Long policyId, AuthPolicyUserFindDto dto) {
    dto.setPolicyId(policyId);
    Page<User> page = authPolicyUserQuery.policyUserList(getSpecification(dto), dto.tranPage());
    return buildVoPageResult(page, AuthPolicyUserAssembler::toUserListVo);
  }

  @Override
  public PageResult<UserListVo> policyUnauthUserList(Long policyId, AuthPolicyUserFindDto dto) {
    dto.setPolicyId(policyId);
    Page<User> page = authPolicyUserQuery.policyUnauthUserList(
        getSpecification(dto), dto.tranPage());
    return buildVoPageResult(page, AuthPolicyUserAssembler::toUserListVo);
  }

  @Override
  public List<IdKey<Long, Object>> userPolicyAdd(Long userId, Set<Long> policyIds) {
    List<AuthPolicyOrg> policyUsers = addToUserPolicy(userId, policyIds);
    return authPolicyUserCmd.userPolicyAdd(userId, policyUsers);
  }

  @Override
  public void userPolicyDelete(Long userId, Set<Long> policyIds) {
    authPolicyUserCmd.userPolicyDelete(userId, policyIds);
  }

  @Override
  public void userPolicyDeleteBatch(HashSet<Long> userIds, HashSet<Long> policyIds) {
    authPolicyUserCmd.userPolicyDeleteBatch(userIds, policyIds);
  }

  @NameJoin
  @Override
  public PageResult<AuthPolicyVo> userPolicyList(Long userId, AuthPolicyFindDto dto) {
    dto.setOrgId(userId).setOrgType(AuthOrgType.USER);
    Page<AuthPolicy> page = authPolicyUserQuery.userPolicyList(
        AuthPolicyAssembler.getSpecification(dto), dto.tranPage());
    return buildVoPageResult(page, AuthPolicyAssembler::toAuthPolicyVo);
  }

  @NameJoin
  @Override
  public PageResult<AuthPolicyAssociatedVo> userAssociatedPolicyList(Long userId,
      AuthPolicyAssociatedFindDto dto) {
    dto.setIgnoreAuthOrg(nonNull(dto.getAdminFullAssociated())
        && dto.getAdminFullAssociated() || dto.getIgnoreAuthOrg());
    dto.setOrgId(userId)/*.setOrgType(AuthOrgType.USER)*/;
    Page<AuthPolicy> page = authPolicyUserQuery.userAssociatedPolicyList(
        AuthPolicyAssembler.getSpecification(dto), dto.tranPage(), false);
    return buildVoPageResult(page, AuthPolicyAssembler::toPolicyAssociatedListVo);
  }

  @NameJoin
  @Override
  public PageResult<PolicyUnauthVo> userUnauthPolicyList(Long userId,
      UnAuthPolicyAssociatedFindDto dto) {
    dto.setOrgId(userId).setOrgType(AuthOrgType.USER)
        .setIgnoreAuthOrg(true).setAdminFullAssociated(true);
    Page<AuthPolicy> page = authPolicyUserQuery.userUnauthPolicyList(
        AuthPolicyAssembler.getSpecification(dto), dto.tranPage());
    return buildVoPageResult(page, AuthPolicyAssembler::toUnAuthPolicyVo);
  }
}
