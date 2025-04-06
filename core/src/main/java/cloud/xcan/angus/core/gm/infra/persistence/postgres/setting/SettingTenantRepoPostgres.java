package cloud.xcan.angus.core.gm.infra.persistence.postgres.setting;

import cloud.xcan.angus.api.commonlink.setting.tenant.SettingTenantRepo;
import org.springframework.stereotype.Repository;

@Repository("settingTenantRepo")
public interface SettingTenantRepoPostgres extends SettingTenantRepo {

}
