package cloud.xcan.angus.api.commonlink.setting.tenant;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository("commonSettingTenantRepo")
public interface SettingTenantRepo extends BaseRepository<SettingTenant, Long> {

  Optional<SettingTenant> findByTenantId(Long tenantId);

  Optional<SettingTenant> findByInvitationCode(String invitationCode);

  boolean existsByTenantId(Long tenantId);

}
