package cloud.xcan.angus.api.manager;

import cloud.xcan.angus.api.commonlink.tenant.Tenant;
import java.util.Collection;
import java.util.List;

public interface TenantManager {

  Tenant checkAndFindTenant(Long tenantId);

  Tenant checkAndFindOwnerTenant();

  List<Tenant> checkAndFind(Collection<Long> tenantIds);

}
