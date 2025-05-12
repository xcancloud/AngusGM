package cloud.xcan.angus.core.gm.application.query.app;

import cloud.xcan.angus.api.commonlink.AuthOrgType;
import cloud.xcan.angus.api.commonlink.dept.Dept;
import cloud.xcan.angus.api.commonlink.group.Group;
import cloud.xcan.angus.api.commonlink.tenant.Tenant;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.core.gm.domain.app.App;
import cloud.xcan.angus.core.gm.domain.policy.AuthPolicy;
import cloud.xcan.angus.core.gm.domain.policy.org.AuthPolicyIdP;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

public interface AppOrgAuthQuery {

  Page<Tenant> appAuthTenant(Long appId, GenericSpecification<Tenant> spec,
      PageRequest pageable);

  Page<User> appAuthUser(Long appId, GenericSpecification<User> spec,
      PageRequest pageable);

  Page<Dept> appAuthDept(Long appId, GenericSpecification<Dept> spec,
      PageRequest pageable);

  Page<Group> appAuthGroup(Long appId, GenericSpecification<Group> spec,
      PageRequest pageable);

  List<AuthPolicy> appAuthGlobal(Long appId);

  Boolean appAuthOrgCheck(Long appId, AuthOrgType orgType, Long orgId);

  List<App> orgAuthApp(AuthOrgType orgType, Long orgId, boolean joinPolicy);

  Boolean orgAuthAppCheck(AuthOrgType orgType, Long orgId, Long appId);

  Page<Tenant> appUnauthTenant(Long appId, GenericSpecification<Tenant> spec,
      PageRequest pageable);

  Page<User> appUnauthUser(Long appId, GenericSpecification<User> spec,
      PageRequest pageable);

  Page<Dept> appUnauthDept(Long appId, GenericSpecification<Dept> spec,
      PageRequest pageable);

  Page<Group> appUnauthGroup(Long appId, GenericSpecification<Group> spec,
      PageRequest pageable);

  Page<AuthPolicy> orgAuthPolicy(Long appId, AuthOrgType orgType, Long orgId,
      GenericSpecification<AuthPolicy> spec, PageRequest pageable);

  Page<AuthPolicy> orgUnauthPolicy(Long appId, AuthOrgType orgType, Long orgId,
      GenericSpecification<AuthPolicy> spec, PageRequest pageable);

  List<Long> getAuthOrgIds(Long tenantId, Long appId, @Nullable AuthOrgType orgType,
      @Nullable Set<String> idsFilter);

  List<Long> getAuthPolicyIds(Long tenantId, Long appId, @Nullable AuthOrgType orgType,
      @Nullable Set<String> orgIdsFilter, @Nullable Set<String> policyIdsFilter);

  Page<AuthPolicyIdP> getAuthPolicyIds(Long tenantId, Long appId,
      @Nullable AuthOrgType orgType, @Nullable Set<String> orgIdsFilter,
      @Nullable Set<String> policyIdsFilter, Pageable pageable);

}
