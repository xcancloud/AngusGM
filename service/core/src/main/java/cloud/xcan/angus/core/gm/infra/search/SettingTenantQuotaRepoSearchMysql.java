package cloud.xcan.angus.core.gm.infra.search;

import cloud.xcan.angus.api.commonlink.setting.tenant.quota.SettingTenantQuota;
import cloud.xcan.angus.core.gm.domain.setting.SettingTenantQuotaRepoSearch;
import cloud.xcan.angus.core.jpa.repository.SimpleSearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public class SettingTenantQuotaRepoSearchMysql extends
    SimpleSearchRepository<SettingTenantQuota> implements
    SettingTenantQuotaRepoSearch {

}
