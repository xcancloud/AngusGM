package cloud.xcan.angus.api.manager;

import cloud.xcan.angus.api.commonlink.setting.quota.QuotaResource;
import cloud.xcan.angus.api.commonlink.setting.tenant.quota.SettingTenantQuota;
import java.util.List;
import java.util.Set;

public interface SettingTenantQuotaManager extends QuotaMessage {

  void checkTenantQuota(QuotaResource quotaResource, Set<?> objectIds, Long num);

  SettingTenantQuota findTenantQuota(Long tenantId, QuotaResource name);

  List<SettingTenantQuota> findTenantQuotas(Long tenantId);
}
