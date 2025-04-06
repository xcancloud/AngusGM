package cloud.xcan.angus.core.gm.application.query.tenant;

import cloud.xcan.angus.api.commonlink.tenant.Tenant;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface TenantQuery {

  Tenant detail(Long id);

  List<Tenant> findAllById(Set<Long> ids);

  Page<Tenant> find(Specification<Tenant> spec, Pageable pageable);

  Tenant find0(Long id);

  Tenant checkAndFind(Long id);

  void checkTenantStatus(String tenantId);

  Set<Long> findCancelExpired(Long count);

  Set<Long> findLockExpire(Long count);

  Set<Long> findUnlockExpire(Long count);

  void checkTenantCanceled(Tenant tenantDb);

  void setUserCount(List<Tenant> tenants);
}
