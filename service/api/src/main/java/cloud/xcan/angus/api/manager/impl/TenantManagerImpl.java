package cloud.xcan.angus.api.manager.impl;

import static cloud.xcan.angus.api.manager.UCManagerMessage.TENANT_NOT_EXISTED_T;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertForbidden;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceNotFound;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getApplicationInfo;
import static cloud.xcan.angus.spec.experimental.BizConstant.OWNER_TENANT_ID;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.tenant.Tenant;
import cloud.xcan.angus.api.commonlink.tenant.TenantRepo;
import cloud.xcan.angus.api.manager.TenantManager;
import cloud.xcan.angus.core.app.verx.VerxRegister;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;

@Biz
public class TenantManagerImpl implements TenantManager {

  @Autowired(required = false)
  private TenantRepo tenantRepo;

  @Override
  public Tenant checkAndFindTenant(Long tenantId) {
    return tenantRepo.findById(tenantId).orElseThrow(() -> ResourceNotFound.of(tenantId, "Tenant"));
  }

  @Override
  public Tenant checkAndFindOwnerTenant() {
    assertForbidden(nonNull(VerxRegister.cacheManager()), "Tenant cache is not initialized");
    // When Cloud Service edition
    if (getApplicationInfo().isCloudServiceEdition()) {
      return checkAndFindTenant(OWNER_TENANT_ID);
    }
    // When privation edition
    Long hid = 0L;
    try {
      hid = VerxRegister.cacheManager().getCon().getHid();
    } catch (Exception exception) {
      assertForbidden("Exception loading tenant from cache");
    }
    return checkAndFindTenant(hid);
  }

  @Override
  public List<Tenant> checkAndFind(Collection<Long> tenantIds) {
    if (isEmpty(tenantIds)) {
      return null;
    }
    List<Tenant> tenants = tenantRepo.findAllByIdIn(tenantIds);
    assertResourceNotFound(tenants, TENANT_NOT_EXISTED_T,
        new Object[]{tenantIds.iterator().next()});
    if (tenantIds.size() != tenants.size()) {
      tenantIds.removeAll(tenants.stream().map(Tenant::getId).collect(Collectors.toSet()));
      assertResourceNotFound(tenantIds.isEmpty(), TENANT_NOT_EXISTED_T,
          new Object[]{tenantIds.iterator().next()});
    }
    return tenants;
  }
}
