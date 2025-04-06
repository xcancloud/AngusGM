package cloud.xcan.angus.api.commonlink.app.open;

import cloud.xcan.angus.core.jpa.repository.AppAuthRepository;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.spec.annotations.CloudServiceEdition;
import cloud.xcan.angus.spec.annotations.PrivateEdition;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository("commonAppAuthRepo")
public interface AppAuthRepo extends BaseRepository<AppAuth, Long>, AppAuthRepository {

  @PrivateEdition
  @Query(value = "SELECT * FROM app_open WHERE app_code = ?1 ORDER BY id DESC LIMIT 1", nativeQuery = true)
  @Override
  AppAuth findLatestByAppCode(String appCode);

  @CloudServiceEdition
  @Query(value = "SELECT * FROM app_open WHERE tenant_id = ?1 AND app_code = ?2 ORDER BY id DESC LIMIT 1", nativeQuery = true)
  @Override
  AppAuth findLatestByTenantIdAndAppCode(Long tenantId, String appCode);

}
