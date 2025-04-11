package cloud.xcan.angus.core.gm.application.query.setting;

import cloud.xcan.angus.api.commonlink.setting.quota.QuotaResource;
import cloud.xcan.angus.api.commonlink.setting.tenant.quota.SettingTenantQuota;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

public interface SettingTenantQuotaQuery {

  SettingTenantQuota detail(String name);

  Page<SettingTenantQuota> find(Specification<SettingTenantQuota> spec, PageRequest pageable);

  void quotaCheck(Map<QuotaResource, Long> dtosMap);

  void quotaExpansionCheck(Map<QuotaResource, Long> dtosMap);

  List<String> appList();

  SettingTenantQuota checkAndFind(String name);

  void setDefaultQuota(Page<SettingTenantQuota> tenantQuotas);

}
