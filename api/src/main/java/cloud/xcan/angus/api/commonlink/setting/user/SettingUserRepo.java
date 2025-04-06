package cloud.xcan.angus.api.commonlink.setting.user;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.spec.annotations.DoInFuture;
import org.springframework.stereotype.Repository;

@DoInFuture("Add cache")
@Repository("commonSettingUserRepo")
public interface SettingUserRepo extends BaseRepository<SettingUser, Long> {

  boolean existsByTenantId(Long tenantId);
}
