package cloud.xcan.angus.core.gm.application.query.setting;

import cloud.xcan.angus.api.commonlink.setting.tenant.quota.SettingTenantQuota;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface SettingTenantQuotaSearch {

  Page<SettingTenantQuota> search(Set<SearchCriteria> searchCriteria, PageRequest pageable,
      Class<SettingTenantQuota> clz, String[] matchSearchFields);
}
