package cloud.xcan.angus.core.gm.application.query.tenant;

import cloud.xcan.angus.api.commonlink.tenant.Tenant;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface TenantQuery {

  Tenant detail(Long id);

  List<Tenant> findAllById(Set<Long> ids);

  Page<Tenant> list(GenericSpecification<Tenant> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match);

  Tenant find0(Long id);

  Tenant checkAndFind(Long id);

  void checkTenantStatus(String tenantId);

  Set<Long> findCancelExpired(Long count);

  Set<Long> findLockExpire(Long count);

  Set<Long> findUnlockExpire(Long count);

  void checkTenantCanceled(Tenant tenantDb);

  void setUserCount(List<Tenant> tenants);

}
