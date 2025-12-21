package cloud.xcan.angus.core.gm.infra.authentication.checker;

import cloud.xcan.angus.core.gm.application.query.authentication.AuthUserQuery;
import cloud.xcan.angus.core.gm.application.query.tenant.TenantQuery;
import cloud.xcan.angus.security.authentication.dao.checker.DefaultPreAuthenticationChecks;
import cloud.xcan.angus.security.model.CustomOAuth2User;
import jakarta.annotation.Resource;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserPreAuthenticationChecks extends DefaultPreAuthenticationChecks {

  @Resource
  private TenantQuery tenantQuery;

  @Resource
  private AuthUserQuery authUserQuery;

  @Override
  public void check(UserDetails user) {
    super.check(user);
    this.tenantQuery.checkTenantStatus(((CustomOAuth2User) user).getTenantId());
    this.authUserQuery.checkOperationPlatformLogin((CustomOAuth2User) user);
  }
}
