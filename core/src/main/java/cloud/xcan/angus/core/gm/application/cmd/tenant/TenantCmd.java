package cloud.xcan.angus.core.gm.application.cmd.tenant;


import cloud.xcan.angus.api.commonlink.tenant.Tenant;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.enums.UserSource;
import cloud.xcan.angus.core.gm.domain.tenant.audit.TenantCertAudit;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.time.LocalDateTime;
import java.util.Set;

public interface TenantCmd {

  IdKey<Long, Object> add(Tenant tenant, TenantCertAudit tenantAudit, User user,
      UserSource userSource);

  void update(Tenant tenant, TenantCertAudit tenantAudit, User user);

  void replace(Tenant tenant, TenantCertAudit tenantAudit, User user);

  void enabled(Long id, Boolean enabled);

  void locked(Long id, Boolean locked, LocalDateTime lockStartDate, LocalDateTime lockEndDate);

  void lockExpire(Set<Long> tenantIds);

  void unlockExpire(Set<Long> unlockExpireTenantIds);

  IdKey<Long, Object> add0(Tenant tenant);

}
