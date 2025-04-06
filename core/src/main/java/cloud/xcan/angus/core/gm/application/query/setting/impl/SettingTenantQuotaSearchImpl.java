package cloud.xcan.angus.core.gm.application.query.setting.impl;

import cloud.xcan.angus.api.commonlink.setting.tenant.quota.SettingTenantQuota;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.setting.SettingTenantQuotaQuery;
import cloud.xcan.angus.core.gm.application.query.setting.SettingTenantQuotaSearch;
import cloud.xcan.angus.core.gm.domain.setting.SettingTenantQuotaRepoSearch;
import cloud.xcan.angus.remote.search.SearchCriteria;
import jakarta.annotation.Resource;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Biz
public class SettingTenantQuotaSearchImpl implements SettingTenantQuotaSearch {

  @Resource
  private SettingTenantQuotaRepoSearch settingTenantQuotaRepoSearch;

  @Resource
  private SettingTenantQuotaQuery settingTenantQuotaQuery;

  @Override
  public Page<SettingTenantQuota> search(Set<SearchCriteria> searchCriteria, PageRequest pageable,
      Class<SettingTenantQuota> clz, String[] matchSearchFields) {
    return new BizTemplate<Page<SettingTenantQuota>>() {

      @Override
      protected Page<SettingTenantQuota> process() {
        Page<SettingTenantQuota> tenantQuotas = settingTenantQuotaRepoSearch
            .find(searchCriteria, pageable, clz, matchSearchFields);
        if (tenantQuotas.hasContent()) {
          // Associated tenant setting default value
          settingTenantQuotaQuery.setDefaultQuota(tenantQuotas);

          // Query and associate used
          // TODO
        }
        return tenantQuotas;
      }
    }.execute();
  }
}
