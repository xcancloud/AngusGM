package cloud.xcan.angus.core.gm.interfaces.policy.facade.internal.assembler;

import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.longSafe;

import cloud.xcan.angus.api.commonlink.AuthOrgType;
import cloud.xcan.angus.api.commonlink.policy.PolicyGrantScope;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.gm.user.vo.UserListVo;
import cloud.xcan.angus.core.gm.domain.policy.org.AuthPolicyOrg;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.org.AuthPolicyUserFindDto;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class AuthPolicyUserAssembler {

  public static List<AuthPolicyOrg> addToPolicyUser(Long policyId, Set<Long> userIds) {
    return userIds.stream().map(userId -> new AuthPolicyOrg().setPolicyId(policyId)
        .setOrgType(AuthOrgType.USER).setOrgId(userId)
        /*.setAppId()*/
        .setGrantScope(PolicyGrantScope.TENANT_ORG)
        .setOpenAuth(false)
        .setTenantId(getOptTenantId())
        .setCreatedBy(longSafe(getUserId(), -1L))
        .setDefault0(false)
        .setCreatedDate(LocalDateTime.now())
    ).collect(Collectors.toList());
  }

  public static List<AuthPolicyOrg> addToUserPolicy(Long userId, Set<Long> policyIds) {
    return policyIds.stream().map(policyId -> new AuthPolicyOrg().setPolicyId(policyId)
        .setOrgType(AuthOrgType.USER).setOrgId(userId)
        /*.setAppId()*/
        .setGrantScope(PolicyGrantScope.TENANT_ORG)
        .setOpenAuth(false)
        .setTenantId(getOptTenantId())
        .setCreatedBy(longSafe(getUserId(), -1L))
        .setDefault0(false)
        .setCreatedDate(LocalDateTime.now())
    ).collect(Collectors.toList());
  }

  public static UserListVo toUserListVo(User user) {
    UserListVo userListVo = new UserListVo().setId(user.getId())
        .setId(user.getId())
        .setUsername(user.getUsername())
        .setFullname(user.getFullname())
        .setFirstName(user.getFirstName())
        .setLastName(user.getLastName())
        .setItc(user.getItc())
        .setCountry(user.getCountry())
        .setMobile(user.getMobile())
        .setEmail(user.getEmail())
        .setLandline(user.getLandline())
        .setAvatar(user.getAvatar())
        .setTitle(user.getTitle())
        .setGender(user.getGender())
        //.setAddress(addressDomain2To(user.getAddress()))
        .setAddress(user.getAddress())
        .setSysAdmin(user.getSysAdmin())
        .setDeptHead(user.getDeptHead())
        .setOnline(user.getOnline())
        .setOnlineDate(user.getOnlineDate())
        .setOfflineDate(user.getOfflineDate())
        .setEnabled(user.getEnabled())
        .setCreatedDate(user.getCreatedDate())
        .setSource(user.getSource())
        .setLocked(user.getLocked())
        .setLockStartDate(user.getLockStartDate())
        .setLockEndDate(user.getLockEndDate())
        .setTenantId(user.getTenantId())
        .setTenantName(user.getTenantName())
        .setCreatedBy(user.getCreatedBy())
        .setCreatedDate(user.getCreatedDate())
        .setLastModifiedBy(user.getLastModifiedBy())
        .setLastModifiedDate(user.getLastModifiedDate());
    return userListVo;
  }

  public static GenericSpecification<User> getSpecification(AuthPolicyUserFindDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate")
        .matchSearchFields("fullname", "mobile", "title", "username")
        .orderByFields("id", "fullname", "createdDate")
        //.inAndNotFields("tagId")
        .build();
    return new GenericSpecification<>(filters);
  }
}
