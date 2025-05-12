package cloud.xcan.angus.api.commonlink.user;

import cloud.xcan.angus.core.jpa.entity.projection.IdAndName;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.jpa.repository.NameJoinRepository;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("commonUserRepo")
public interface UserRepo extends NameJoinRepository<User, Long>, BaseRepository<User, Long> {

  @Override
  List<User> findByIdIn(Collection<Long> ids);

  @Query(value = "SELECT u.id FROM user0 u WHERE u.deleted = 0 AND u.enabled = 1 AND u.id IN ?1", nativeQuery = true)
  List<Long> findIdsByIdIn(Collection<Long> ids);

  @Query(value = "SELECT u.id FROM user0 u WHERE u.deleted = 0 AND u.enabled = 1 AND u.username IN ?1", nativeQuery = true)
  List<Long> findIdsByUsernameIn(Collection<String> usernames);

  @Query(value = "SELECT u.username FROM user0 u WHERE u.deleted = 0 AND u.enabled = 1 AND u.online = ?1", nativeQuery = true)
  List<String> findUsernamesByOnline(Boolean online);

  @Query(value = "SELECT u.id FROM user0 u WHERE u.deleted = 0 AND u.enabled = 1 AND u.tenant_id = ?1 AND u.online = ?2", nativeQuery = true)
  List<Long> findIdsByTenantIdAndOnline(Collection<Long> tenantIds, Boolean online);

  @Query(value = "SELECT u.username FROM user0 u WHERE u.deleted = 0 AND u.enabled = 1 AND u.tenant_id = ?1 AND u.online = ?2", nativeQuery = true)
  List<String> findUsernamesByTenantIdAndOnline(Collection<Long> tenantIds, Boolean online);

  @Query(value = "SELECT u.username FROM user0 u WHERE u.deleted = 0 AND u.enabled = 1 AND u.id = ?1 AND u.online = ?2", nativeQuery = true)
  List<String> findUsernamesByIdAndOnline(Collection<Long> ids, Boolean online);

  @Override
  Page<User> findAll(Specification<User> spc, Pageable pageable);

  Page<User> findAllByTenantId(Long tenantId, Pageable page);

  List<User> findAllByTenantId(Long tenantId);

  @Query(value = "SELECT u FROM User u "
      + "WHERE u.tenantId = ?1 AND u.deleted = false AND u.enabled = true AND u.expired = false AND u.locked = false")
  List<User> findValidByTenantId(Long tenantId);

  List<User> findByTenantIdAndDirectoryId(Long tenantId, Long directoryId);

  @Query(value = "SELECT u FROM User u "
      + "WHERE u.tenantId = ?1 AND u.sysAdmin = true AND u.deleted = false AND u.enabled = true AND u.expired = false AND u.locked = false")
  List<User> findValidSysAdminByTenantId(Long tenantId);

  @Query(value = "SELECT u.id FROM User u "
      + "WHERE u.tenantId = ?1 AND u.sysAdmin = true AND u.deleted = false AND u.enabled = true AND u.expired = false AND u.locked = false")
  List<Long> findValidSysAdminIdsByTenantId(Long tenantId);

  List<User> findByTenantIdAndSysAdmin(Long tenantId, boolean sysAdmin);

  List<User> findByUsernameAndIdNot(String username, Long id);

  List<User> findByMobileAndIdNot(String mobile, Long id);

  List<User> findByEmailAndIdNot(String email, Long id);

  long countByTenantId(Long tenantId);

  Page<User> findAllByTenantIdInAndEnabled(Collection<?> tenantIds, Boolean enabled,
      Pageable page);

  @Query(value = "SELECT u.id FROM user0 u WHERE u.deleted = 0 AND u.enabled =1", nativeQuery = true)
  Page<BigInteger> findValidId(Pageable page);

  @Query(value = "SELECT u.id FROM user0 u WHERE u.tenant_id = ?1  and u.deleted = 0 AND u.enabled =1", nativeQuery = true)
  Page<BigInteger> findValidIdByTenantId(Long tenantId, Pageable page);

  @Query(value = "SELECT u.email FROM user0 u WHERE u.tenant_id IN (?1)  and u.deleted = 0 AND u.enabled =1", nativeQuery = true)
  Page<String> findValidEmailByTenantIdIn(Collection<?> tenantIds, Pageable page);

  @Query(value = "SELECT u.email FROM user0 u WHERE u.tenant_id = ?1  and u.deleted = 0 AND u.enabled =1", nativeQuery = true)
  Page<String> findValidEmailByTenantId(Long tenantId, Pageable page);

  @Query(value = "SELECT u.email FROM user0 u WHERE u.id IN (?1)  and u.deleted = 0 AND u.enabled =1", nativeQuery = true)
  List<String> findValidEmailByIdIn(Collection<?> ids);

  @Query(value = "SELECT u.email FROM user0 u WHERE u.deleted = 0 AND u.enabled =1", nativeQuery = true)
  Page<String> findValidAllEmail(Pageable page);

  @Query(value = "SELECT u.mobile FROM user0 u WHERE u.tenant_id IN (?1)  and u.deleted = 0 AND u.enabled =1", nativeQuery = true)
  Page<String> findValidMobileByTenantIdIn(Collection<?> tenantIds, Pageable page);

  @Query(value = "SELECT u.mobile FROM user0 u WHERE u.tenant_id = ?1  and u.deleted = 0 AND u.enabled =1", nativeQuery = true)
  Page<String> findValidMobileByTenantId(Long tenantId, Pageable page);

  @Query(value = "SELECT u.mobile FROM user0 u WHERE u.id IN (?1) AND u.deleted = 0 AND u.enabled =1", nativeQuery = true)
  List<String> findValidMobileByIdIn(Collection<?> ids);

  @Query(value = "SELECT u.mobile FROM user0 u WHERE u.deleted = 0 AND u.enabled =1", nativeQuery = true)
  Page<String> findValidAllMobile(Pageable page);

  Page<User> findAllByEnabled(Boolean enabled, Pageable page);

  Page<User> findAllByEnabledAndIdIn(Boolean enabled, Collection<?> ids, Pageable page);

  Page<User> findAllByEnabledAndIdNotIn(Boolean enabled, Collection<?> ids, Pageable page);

  List<User> findAllByEnabledAndIdIn(Boolean enabled, Collection<?> ids);

  List<User> findAllByMobileIn(Set<String> mobiles);

  List<User> findAllByEmailIn(Set<String> emails);

  List<User> findByMobileOrEmail(String mobile, String email);

  List<User> findByMobileOrEmailOrUsername(String mobile, String email, String username);

  List<User> findByEmail(String email);

  List<User> findByMobile(String mobile);

  List<User> findAllByUsername(String username);

  long countBySignupAccountAndSignupAccountType(String signupAccount, SignupType signupType);

  List<User> findBySignupAccountTypeAndEnabledAndSysAdminAndTenantId(
      SignupType signupAccountType, Boolean enabled, Boolean admin, Long tenantId);

  @Query(value = "SELECT u FROM User u "
      + "where u.id IN (?1) AND u.deleted = false AND u.enabled = true AND u.expired = false AND u.locked = false")
  List<User> findValidByIdIn(Collection<?> ids);

  @Query(value = "SELECT u FROM User u "
      + "where u.id = ?1 AND u.deleted = false AND u.enabled = true AND u.expired = false AND u.locked = false")
  Optional<User> findValidById(Long id);

  @Query(value = "SELECT u.id FROM user0 u WHERE u.id IN (?1)", nativeQuery = true)
  List<Long> findUserIdsByIdIn(Collection<?> ids);

  @Query(value = "SELECT u.id FROM user0 u WHERE u.directory_id = ?1", nativeQuery = true)
  Set<Long> findUserIdsByDirectoryId(Long id);

  @Query(value = "SELECT u.id FROM user0 u WHERE u.id IN (?1) AND u.deleted = 0 AND u.enabled = 1 AND u.expired = 0 AND u.locked = 0", nativeQuery = true)
  List<Long> findValidUserIdsByIdIn(Collection<?> ids);

  @Query(value = "SELECT du.dept_id as orgId FROM dept_user du WHERE du.user_id = ?1 "
      + "UNION SELECT gu.group_id as orgId FROM group_user gu WHERE gu.user_id = ?1", nativeQuery = true)
  List<Long> findOrgIdsById(Long id);

  @Query(value = "SELECT id, full_name name FROM user0 WHERE id IN ?1 UNION SELECT id, name FROM dept WHERE id IN ?1 UNION SELECT id, name FROM group0 WHERE id IN ?1", nativeQuery = true)
  List<IdAndName> findOrgIdAndNameByIds(Collection<?> orgIds);

  @Query(value = "SELECT du.dept_id as orgId FROM dept_user du WHERE du.user_id = ?1 "
      + "UNION SELECT gu.group_id as orgId FROM group_user gu, group0 g WHERE gu.group_id = g.id AND gu.user_id = ?1 AND g.enabled = 1", nativeQuery = true)
  List<Long> findValidOrgIdsById(Long id);

  @Query(value = "SELECT * FROM user0 u WHERE u.sys_admin = 1 AND u.enabled = 1 AND u.deleted = 0 AND u.locked = 0  AND u.tenant_id = ?1 ORDER BY u.id ASC LIMIT 1", nativeQuery = true)
  Optional<User> findMainSysAdminUser(Long tenantId);

  @Query(value = "SELECT * FROM user0 u WHERE u.sys_admin = 1 AND u.enabled = 1 AND u.deleted = 0 AND u.locked = 0 AND u.tenant_id = ?1", nativeQuery = true)
  List<User> findAllSysAdminUser(Long tenantId);

  @Query(value = "SELECT u.id FROM user0 u WHERE u.sys_admin = 1 AND u.enabled = 1 AND u.deleted = 0 AND u.locked = 0 AND u.tenant_id = ?1", nativeQuery = true)
  List<Long> findIdsSysAdminUser(Long tenantId);

  @Query(value = "SELECT count(*) FROM user0 u WHERE u.sys_admin = 1 AND u.enabled = 1 AND u.deleted = 0 AND u.locked = 0 AND u.tenant_id = ?1", nativeQuery = true)
  int countValidSysAdminUser(Long tenantId);

  @Query(value = "SELECT * FROM user0 u WHERE u.id = ?1", nativeQuery = true)
  User findByUserId(Long id);

  @Query(value = "SELECT u FROM User u WHERE u.fullName like %?1% and  u.enabled = true")
  List<User> findByFullNameLike(String fullName);

  @Query(value = "SELECT * FROM user0 WHERE lock_start_date <= ?1 AND locked = 0 AND (lock_end_date >= ?1 OR lock_start_date is NULL) LIMIT ?2", nativeQuery = true)
  Set<Long> findLockExpire(LocalDateTime now, Long count);

  @Query(value = "SELECT * FROM user0 WHERE lock_end_date <= ?1 AND locked = 1 LIMIT ?2", nativeQuery = true)
  Set<Long> findUnockExpire(LocalDateTime now, Long count);

  @Modifying
  @Query("UPDATE User SET deptHead=?2 WHERE id=?1")
  void updateDeptHead(Long userId, Boolean head);

  @Modifying
  @Query("UPDATE User SET tenantName=?2 WHERE tenantId=?1")
  void updateTenantNameByTenantId(Long tenantId, String tenantName);

  @Modifying
  @Query(value = "update user0 t set t.locked = 1, last_lock_date = NOW() WHERE t.id IN (?1)", nativeQuery = true)
  void updateLockStatusByIdIn(Collection<Long> userIds);

  @Modifying
  @Query(value = "update user0 t set t.locked = 0, lock_start_date = null, lock_end_date = null WHERE t.id IN (?1)", nativeQuery = true)
  void updateUnlockStatusByIdIn(Collection<Long> userIds);

  @Modifying
  @Query("UPDATE User t set t.mainDeptId = null WHERE t.mainDeptId IN (?1)")
  void updateMainDeptByDeptIdIn(Collection<Long> deptIds);

  @Modifying
  @Query("UPDATE User t set t.mainDeptId = null WHERE t.id = ?1 AND t.mainDeptId IN ?2")
  void updateMainDeptByUserIdAndDeptIdIn(Long usertId, Collection<Long> deptIds);

  @Modifying
  @Query("UPDATE User t set t.mainDeptId = null WHERE t.id IN (?2) AND t.mainDeptId = ?1")
  void updateMainDeptByDeptIdAndUserIdIn(Long deptId, Collection<Long> userIds);

  @Transactional(rollbackFor = Exception.class) /* Fix:: javax.persistence.TransactionRequiredException: Executing an update/delete query */
  @Modifying
  @Query(value = "UPDATE user0 t set t.directory_id = null WHERE t.id IN (?1)", nativeQuery = true)
  void updateDirectoryEmptyByUserIdIn(Set<Long> userIds);

  @Transactional(rollbackFor = Exception.class) /* Fix:: javax.persistence.TransactionRequiredException: Executing an update/delete query */
  @Modifying
  @Query(value = "UPDATE user0 t set t.directory_id = null WHERE t.directory_id = ?1", nativeQuery = true)
  void updateDirectoryEmptyByDirectoryId(Long id);

  @Modifying
  @Query("update User a set a.online = true, a.onlineDate = now() where a.id in ?1")
  void updateOnlineStatus(List<Long> userId);

  @Modifying
  @Query("update User a set a.online = false, a.offlineDate = now() where a.id in ?1")
  void updateOfflineStatus(List<Long> userId);

  @Modifying
  @Query(value = "UPDATE user0 SET mobile='', email='', username=UUID(), deleted=1 WHERE tenant_id IN (?1)", nativeQuery = true)
  void deleteByTenantIdIn(Collection<Long> ids);

  @Modifying
  @Query(value = "UPDATE user0 SET mobile='', email='', username=UUID(), deleted=1 WHERE id IN (?1)", nativeQuery = true)
  void deleteByIdIn(Collection<Long> ids);

  @Modifying
  @Query(value = "DELETE FROM user0 WHERE directory_id = ?1 AND source = ?2", nativeQuery = true)
  void deleteByDirectoryIdAndSource(Long id, String value);

  @Modifying
  @Query(value = "DELETE FROM user0 WHERE id IN ?1", nativeQuery = true)
  void deleteAllByIdIn(Collection<Long> ids);


}
