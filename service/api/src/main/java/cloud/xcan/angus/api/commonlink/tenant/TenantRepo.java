package cloud.xcan.angus.api.commonlink.tenant;

import cloud.xcan.angus.api.enums.TenantStatus;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.jpa.repository.NameJoinRepository;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository("commonTenantRepo")
public interface TenantRepo extends NameJoinRepository<Tenant, Long>, BaseRepository<Tenant, Long> {

  List<Tenant> findAllByRealNameStatus(TenantRealNameStatus auditStatus);

  @Query(value = "SELECT * FROM tenant WHERE id IN ?1", nativeQuery = true)
  List<Tenant> findAllByIdIn(Collection<Long> tenantIds);

  Optional<Tenant> findFirstByStatusAndLocked(TenantStatus status, Boolean locked);

  @Query(value = "SELECT DISTINCT t.* FROM tenant t, user0 u WHERE t.id = u.tenant_id AND u.id IN (?1)", nativeQuery = true)
  List<Tenant> findAllByUserIdIn(Collection<Long> userIds);

  @Query(value = "SELECT id FROM tenant WHERE apply_cancel_date <= ?1 AND `status` = 'CANCELLING' LIMIT ?2", nativeQuery = true)
  Set<Long> findCancelExpiredTenant(LocalDateTime date, Long count);

  @Query(value = "SELECT * FROM tenant WHERE lock_start_date <= ?1 AND locked = 0"
      + " AND (lock_end_date >= ?1 OR lock_start_date is NULL) LIMIT ?2", nativeQuery = true)
  Set<Long> findLockExpire(LocalDateTime now, Long count);

  @Query(value = "SELECT * FROM tenant WHERE lock_end_date <= ?1 AND locked = 1 LIMIT ?2", nativeQuery = true)
  Set<Long> findUnockExpire(LocalDateTime now, Long count);

  @Query(value = "SELECT id FROM Tenant")
  Page<Long> findAllIds(Pageable pageable);

  @Modifying
  @Query(value = "UPDATE tenant t set t.status = ?2 WHERE t.id IN (?1)", nativeQuery = true)
  void updateStatusByTenantIdIn(Set<Long> tenantIds, String status);

  @Modifying
  @Query(value = "UPDATE tenant t set t.locked = 1, t.last_lock_date = NOW() WHERE t.id IN (?1)", nativeQuery = true)
  void updateLockStatusByIdIn(Set<Long> tenantIds);

  @Modifying
  @Query(value = "UPDATE tenant t set t.locked = 0, t.lock_start_date = null, t.lock_end_date = null WHERE t.id IN (?1)", nativeQuery = true)
  void updateUnlockStatusByIdIn(Set<Long> tenantIds);

}
