package cloud.xcan.angus.core.gm.application.converter;

import static cloud.xcan.angus.core.gm.application.query.tenant.impl.TenantQueryImpl.genTenantNo;

import cloud.xcan.angus.api.commonlink.tenant.Tenant;
import cloud.xcan.angus.api.commonlink.tenant.TenantRealNameStatus;
import cloud.xcan.angus.api.commonlink.tenant.TenantType;
import cloud.xcan.angus.api.enums.TenantSource;
import cloud.xcan.angus.api.enums.TenantStatus;

public class TenantConverter {

  public static Tenant toSignupTenant(TenantSource source) {
    String tenantNo = genTenantNo();
    Tenant tenant = new Tenant()
        .setName(tenantNo)
        .setType(TenantType.UNKNOWN)
        .setLocked(false)
        .setSource(source)
        .setRealNameStatus(TenantRealNameStatus.NOT_SUBMITTED)
        .setNo(tenantNo)
        .setStatus(TenantStatus.ENABLED)
        .setAddress("")
        .setRemark("");
    tenant.setCreatedBy(-1L);
    tenant.setLastModifiedBy(-1L);
    return tenant;
  }

  public static void assembleTenantInfo(Tenant tenantDb, Tenant tenant) {
    if (!tenantDb.isRealNamePassed()) {
      // Cannot modify after real name
      tenantDb.setName(tenant.getName());
    }
    tenantDb.setRemark(tenant.getRemark()).setAddress(tenantDb.getAddress());
  }
}
