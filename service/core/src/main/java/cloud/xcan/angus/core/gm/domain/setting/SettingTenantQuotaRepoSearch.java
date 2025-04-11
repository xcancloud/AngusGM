package cloud.xcan.angus.core.gm.domain.setting;

import cloud.xcan.angus.api.commonlink.setting.tenant.quota.SettingTenantQuota;
import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface SettingTenantQuotaRepoSearch extends CustomBaseRepository<SettingTenantQuota> {

}
