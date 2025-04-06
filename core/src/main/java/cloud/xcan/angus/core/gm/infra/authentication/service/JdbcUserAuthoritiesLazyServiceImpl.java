package cloud.xcan.angus.core.gm.infra.authentication.service;

import static cloud.xcan.angus.api.commonlink.client.ClientSource.isOperationClientSignIn;
import static cloud.xcan.angus.spec.experimental.BizConstant.AuthKey.POLICY_PREFIX;
import static cloud.xcan.angus.spec.experimental.BizConstant.AuthKey.ROLE_TOP_PREFIX;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isBlank;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.to.TORoleUserRepo;
import cloud.xcan.angus.api.manager.UserManager;
import cloud.xcan.angus.core.gm.domain.policy.org.AuthPolicyOrgRepo;
import cloud.xcan.angus.core.jpa.entity.projection.IdAndCode;
import cloud.xcan.angus.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import cloud.xcan.angus.security.model.CustomOAuth2User;
import cloud.xcan.angus.security.repository.JdbcUserAuthoritiesLazyService;
import cloud.xcan.angus.security.repository.JdbcUserDetailsRepository;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * Lazy initialization and loading of authorization policies, resource, and operational role
 * permissions implement.
 *
 * @see AbstractUserDetailsAuthenticationProvider#authenticate(Authentication)
 * @see JdbcUserDetailsRepository#findByAccount(String)
 */
public class JdbcUserAuthoritiesLazyServiceImpl implements JdbcUserAuthoritiesLazyService {

  @Resource
  private TORoleUserRepo toRoleUserRepo;

  @Resource
  private AuthPolicyOrgRepo authPolicyOrgRepo;

  @Resource
  private UserManager userManager;

  @Override
  public Set<GrantedAuthority> lazyUserAuthorities(CustomOAuth2User user) {
    Set<GrantedAuthority> authorities = new HashSet<>();
    // 1. Assemble the operation application functions authorization polices of user
    boolean isSysAdmin = user.isSysAdmin();
    Long tenantId = Long.valueOf(user.getTenantId());
    List<Long> allOrgIds = userManager.getValidOrgAndUserIds(Long.valueOf(user.getId()));
    allOrgIds.add(tenantId);
    List<IdAndCode> authPolicies = isSysAdmin ?
        authPolicyOrgRepo.findAuthOfSysAdminUser(tenantId, user.getClientId(), allOrgIds)
        : authPolicyOrgRepo.findAuthOfNonSysAdminUser(tenantId, user.getClientId(), allOrgIds);
    initPolicyAuthorities(POLICY_PREFIX, authorities, authPolicies.stream().map(IdAndCode::getCode)
        .collect(Collectors.toList()));

    // 2. Assemble the apis resource authorization of user
    // TODO loading from oauth2_api_authority

    // 3. Assemble the operation tenant authorization roles of user
    if (isOperationClientSignIn(user.getClientSource()) && user.isToUser()) {
      List<String> grantToPolicies = toRoleUserRepo.findUserGrantRoles(
          Long.valueOf(user.getId()));
      initPolicyAuthorities(ROLE_TOP_PREFIX, authorities, grantToPolicies);
    }
    return authorities;
  }

  private void initPolicyAuthorities(@Nullable String policyPrefix,
      Set<GrantedAuthority> authorities,
      List<String> policyCodes) {
    if (isNotEmpty(policyCodes)) {
      for (String policyCode : policyCodes) {
        if (isBlank(policyPrefix) || policyCode.startsWith(policyPrefix)) {
          authorities.add(new SimpleGrantedAuthority(policyCode));
        } else {
          authorities.add(new SimpleGrantedAuthority(policyPrefix + policyCode));
        }
      }
    }
  }

  private void initApiResourceAuthorities(Set<GrantedAuthority> authorities,
      List<String> apiResources) {
    if (isNotEmpty(apiResources)) {
      for (String apiResource : apiResources) {
        if (isNotEmpty(apiResource)) {
          authorities.add(new SimpleGrantedAuthority(apiResource));
        }
      }
    }
  }

}
