package cloud.xcan.angus.api.commonlink.setting.tenant.quota;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.spec.annotations.DoInFuture;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@DoInFuture("Add cache")
@Repository("commonTenantQuotaRepo")
public interface SettingTenantQuotaRepo extends BaseRepository<SettingTenantQuota, Long> {

  List<SettingTenantQuota> findByTenantId(Long optTenantId);

  @Query(value = "SELECT * FROM c_setting_tenant_quota WHERE tenant_id = ?1 AND name = ?2", nativeQuery = true)
  Optional<SettingTenantQuota> findByTenantIdAndName(Long optTenantId, String name);

  boolean existsByTenantId(Long tenantId);

  @Query(value = "SELECT DISTINCT app_code FROM c_setting_tenant_quota WHERE tenant_id = ?1", nativeQuery = true)
  List<String> findAppByTenantId(Long optTenantId);

  @Query(value = "SELECT DISTINCT tenant_id FROM c_setting_tenant_quota WHERE tenant_id IN ?1 AND name = ?2", nativeQuery = true)
  List<Long> findInitializedTenantIds(Collection<Long> tenantIds, String name);
}
