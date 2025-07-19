package cloud.xcan.angus.core.gm.application.query.setting;

import cloud.xcan.angus.api.commonlink.setting.quota.QuotaResource;
import cloud.xcan.angus.api.commonlink.setting.tenant.quota.SettingTenantQuota;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface SettingTenantQuotaQuery {

  SettingTenantQuota detail(String name);

  Page<SettingTenantQuota> list(GenericSpecification<SettingTenantQuota> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match);

  void quotaCheck(Map<QuotaResource, Long> dtosMap);

  void quotaExpansionCheck(Map<QuotaResource, Long> dtosMap);

  List<String> appList();

  SettingTenantQuota checkAndFind(String name);

  void setDefaultQuota(Page<SettingTenantQuota> tenantQuotas);

}
