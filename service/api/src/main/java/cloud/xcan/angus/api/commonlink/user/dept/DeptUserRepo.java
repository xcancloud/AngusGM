package cloud.xcan.angus.api.commonlink.user.dept;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository("commonDeptUserRepo")
public interface DeptUserRepo extends BaseRepository<DeptUser, Long> {

  List<DeptUser> findAllByDeptIdIn(List<Long> deptIds);

  List<DeptUser> findAllByDeptId(Long deptId);

  List<DeptUser> findAllByUserId(Long userId);

  long countByTenantIdAndDeptId(Long tenantId, Long deptId);

  long countByTenantIdAndUserId(Long tenantId, Long userId);

  long countByTenantIdAndDeptIdIn(Long tenantId, Collection<Long> deptIds);

  @Query(value = "SELECT count(ud.userId)as userNum,ud.deptId as deptId FROM DeptUser ud WHERE ud.deptId IN (?1) GROUP BY ud.deptId")
  List<DeptUserNum> selectDeptUserNumsGroupByDeptId(Set<Long> deptIds);

  @Query(value =
      "SELECT u.email FROM user0 u INNER JOIN dept_user du ON du.user_id = u.id "
          + "AND du.dept_id IN (?1) AND u.deleted = 0 AND u.enabled = 1 ORDER BY u.id ASC LIMIT ?2,?3", nativeQuery = true)
  List<String> findValidEmailByDeptIds(Collection<Long> deptIds, int offset, int size);

  @Query(value =
      "SELECT u.mobile FROM user0 u INNER JOIN dept_user du ON du.user_id = u.id "
          + "AND du.dept_id IN (?1) AND u.deleted = 0 AND u.enabled = 1 ORDER BY u.id ASC LIMIT ?2,?3", nativeQuery = true)
  List<String> findValidMobileByDeptIds(Collection<Long> deptIds, int offset, int size);

  @Query(value =
      "SELECT u.id FROM dept_user du INNER JOIN user0 u ON du.user_id = u.id "
          + "AND du.dept_id IN (?1) AND u.deleted = 0 AND u.enabled = 1", nativeQuery = true)
  Set<Long> findValidUserIdsByDeptIds(Collection<Long> deptIds);

  @Query(value =
      "SELECT u.username FROM dept_user du INNER JOIN user0 u ON du.user_id = u.id "
          + "AND du.dept_id IN (?1) AND u.deleted = 0 AND u.enabled = 1 AND u.online = ?2", nativeQuery = true)
  Set<String> findUsernamesByDeptIdInAndOnline(Collection<Long> deptIds, Boolean online);

  @Query(value =
      "SELECT u.id FROM dept_user du INNER JOIN user0 u ON du.user_id = u.id "
          + "AND du.tenant_id = ?1 AND u.tenant_id = ?1 AND du.dept_id IN (?2) AND u.deleted = 0 AND u.enabled = 1", nativeQuery = true)
  Set<Long> findValidUserIdsByTenantIdAndDeptIds(Long tenantId, Collection<Long> deptIds);

  @Query(value = "SELECT du.user_id FROM dept_user du WHERE du.dept_id IN (?1) ", nativeQuery = true)
  Set<Long> findUserIdsByDeptIds(Collection<Long> deptIds);

  @Modifying
  @Query("UPDATE DeptUser set deptHead=?2 WHERE deptId=?1")
  void updateDeptHead(Long deptId, Boolean deptHead);

  @Modifying
  @Query("UPDATE DeptUser set deptHead=?3 WHERE userId=?2 AND deptId=?1")
  void updateDeptHead(Long deptId, Long userId, Boolean deptHead);

  @Modifying
  @Query("DELETE FROM DeptUser ud WHERE ud.userId in (?1)")
  void deleteAllByUserIdIn(Set<Long> userIds);

  @Modifying
  @Query("DELETE FROM DeptUser ud WHERE ud.deptId = ?1 AND ud.userId in (?2)")
  void deleteByDeptIdAndUserIdIn(Long deptId, Set<Long> userIds);

  @Modifying
  @Query("DELETE FROM DeptUser ud WHERE ud.userId = ?1 AND ud.deptId in (?2)")
  void deleteByUserIdAndDeptIdIn(Long userId, Set<Long> deptIds);

  @Modifying
  @Query("DELETE FROM DeptUser ud WHERE ud.deptId in (?1) AND ud.userId in (?2)")
  void deleteByDeptIdInAndUserIdIn(Set<Long> deptIds, Set<Long> userIds);

  @Modifying
  @Query("DELETE FROM DeptUser ud WHERE ud.deptId in (?1)")
  void deleteAllByDeptIdIn(Collection<Long> deptIds);

  @Modifying
  @Query("DELETE FROM DeptUser ud WHERE ud.tenantId in (?1)")
  void deleteAllByTenantIdIn(Set<Long> tenantIds);

}
