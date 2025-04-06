package cloud.xcan.angus.core.gm.infra.persistence.postgres.setting;

import cloud.xcan.angus.api.commonlink.setting.SettingRepo;
import org.springframework.stereotype.Repository;

@Repository("settingRepo")
public interface SettingRepoPostgres extends SettingRepo {

}
