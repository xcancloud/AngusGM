package cloud.xcan.angus.api.commonlink.app.open;

import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository("commonAppOpenRepo")
public interface AppOpenRepo extends BaseRepository<AppOpen, Long> {

  /**
   * Unique index protection, only one activation record is allowed
   */
  AppOpen findByTenantIdAndEditionTypeAndAppCodeAndVersion(Long tenantId, EditionType editionType,
      String appCode, String version);

  List<AppOpen> findByAppIdInAndTenantId(Collection<Long> appIds, Long tenantId);

  @Query(value = "SELECT * FROM app_open WHERE tenant_id = ?1 AND client_id = ?2 AND expiration_deleted = false", nativeQuery = true)
  List<AppOpen> findValidByTenantId(Long tenantId, String clientId);

  @Query(value = "SELECT app_id FROM app_open WHERE tenant_id = ?1 AND client_id = ?2 AND expiration_deleted = false", nativeQuery = true)
  List<Long> findValidAppIdsByTenantId(Long tenantId, String clientId);

  @Query(value = "SELECT app_id FROM app_open WHERE tenant_id = ?1 AND edition_type = ?2 "
      + "AND client_id = ?3 AND expiration_deleted = false", nativeQuery = true)
  List<Long> findValidAppIdsByTenantIdAdnEditionType(Long tenantId, String editionType,
      String clientId);

  @Query(value = "SELECT DISTINCT tenant_id FROM auth_user WHERE tenant_id NOT IN "
      + "(SELECT DISTINCT tenant_id FROM app_open WHERE app_id = ?1)", nativeQuery = true)
  List<Long> findUnopenSignupTenantIdByAppId(Long appId);

  @Query(value =
      "SELECT DISTINCT tenant_id FROM auth_user WHERE tenant_real_name_status ='AUDITED' AND tenant_id NOT IN "
          + "(SELECT DISTINCT tenant_id FROM app_open WHERE app_id = ?1)", nativeQuery = true)
  List<Long> findUnopenRealnameTenantIdByAppId(Long appId);

  @Modifying
  @Query(value = "UPDATE app_open SET expiration_deleted = 1 WHERE expiration_date < ?1 AND expiration_deleted =0 ", nativeQuery = true)
  void updateExpiredByBeforeDate(LocalDateTime now);

  @Modifying
  @Query(value = "DELETE FROM app_open WHERE app_id IN ?1", nativeQuery = true)
  void deleteByAppIdIn(Collection<Long> ids);
}
