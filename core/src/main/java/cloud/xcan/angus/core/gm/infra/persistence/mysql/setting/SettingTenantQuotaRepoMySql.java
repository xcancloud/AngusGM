package cloud.xcan.angus.core.gm.infra.persistence.mysql.setting;

import cloud.xcan.angus.api.commonlink.setting.tenant.quota.SettingTenantQuotaRepo;
import org.springframework.stereotype.Repository;

@Repository("settingTenantQuotaRepo")
public interface SettingTenantQuotaRepoMySql extends SettingTenantQuotaRepo {

}
