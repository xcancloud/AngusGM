package cloud.xcan.angus.core.gm.infra.persistence.postgres.setting;

import cloud.xcan.angus.api.commonlink.setting.user.SettingUserRepo;
import org.springframework.stereotype.Repository;

@Repository("settingUserRepo")
public interface SettingUserRepoPostgres extends SettingUserRepo {

}
