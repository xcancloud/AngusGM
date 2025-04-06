package cloud.xcan.angus.api.commonlink.user.group;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository("commonGroupUserRepo")
public interface GroupUserRepo extends BaseRepository<GroupUser, Long> {

  List<GroupUser> findAllByGroupIdIn(List<Long> groupIds);

  List<GroupUser> findAllByGroupId(Long groupId);

  List<GroupUser> findByTenantIdAndDirectoryId(Long tenantId, Long directoryId);

  List<GroupUser> findByTenantId(Long tenantId);

  List<GroupUser> findAllByUserId(Long userId);

  long countByTenantIdAndGroupId(Long tenantId, Long groupId);

  long countByTenantIdAndUserId(Long tenantId, Long userId);

  @Query(value = "SELECT count(ug.userId) as userNum,ug.groupId as groupId FROM GroupUser ug WHERE ug.groupId IN (?1) GROUP BY ug.groupId")
  List<GroupUserNum> selectGroupUserNumsGroupByGroupId(Set<Long> groupIds);

  @Query(value = "SELECT u.email FROM user0 u INNER JOIN group_user gu ON u.id = gu.user_id "
      + "AND gu.group_id IN (?1) AND u.deleted = 0 AND u.enabled = 1 ORDER BY u.id ASC LIMIT ?2,?3", nativeQuery = true)
  List<String> findValidEmailByGroupIds(Collection<Long> groupIds, int offset, int size);

  @Query(value = "SELECT u.mobile FROM user0 u INNER JOIN group_user gu ON u.id = gu.user_id "
      + "AND gu.group_id IN (?1) AND u.deleted = 0 AND u.enabled = 1 ORDER BY u.id ASC LIMIT ?2,?3", nativeQuery = true)
  List<String> findValidMobileByGroupIds(Collection<Long> groupIds, int offset, int size);

  @Query(value = "SELECT u.id FROM group_user gu INNER JOIN user0 u ON gu.user_id = u.id "
      + "AND gu.group_id IN (?1) AND u.deleted = 0 AND u.enabled = 1", nativeQuery = true)
  Set<Long> findValidUserIdsByGroupIds(Collection<Long> groupIds);

  @Query(value = "SELECT u.id FROM group_user gu INNER JOIN user0 u ON gu.user_id = u.id "
      + "AND gu.tenant_id = ?1 AND u.tenant_id = ?1 AND gu.group_id IN (?2) AND u.deleted = 0 AND u.enabled = 1", nativeQuery = true)
  Set<Long> findValidUserIdsByTenantIdAndGroupIds(Long tenantId, Collection<Long> groupIds);

  @Query(value =
      "SELECT u.username FROM group_user gu INNER JOIN user0 u ON du.user_id = u.id "
          + "AND gu.group_id IN (?1) AND u.deleted = 0 AND u.enabled = 1 AND u.online = ?2", nativeQuery = true)
  Set<String> findUsernamesByGroupIdInAndOnline(Collection<Long> groupIds, Boolean online);

  @Query(value = "SELECT gu.user_id FROM group_user gu WHERE gu.group_id IN (?1) ", nativeQuery = true)
  Set<Long> findUserIdsByGroupIds(Collection<Long> groupIds);

  @Modifying
  @Query("DELETE FROM GroupUser ug WHERE ug.groupId = ?1 AND ug.userId in (?2)")
  void deleteByGroupIdAndUserId(Long groupId, Collection<Long> userIds);

  @Modifying
  @Query("DELETE FROM GroupUser ug WHERE ug.directoryId = ?1 AND  ug.groupId IN (?2) AND ug.userId in (?3)")
  void deleteByDirectoryIdAndGroupIdInAndUserIdIn(Long directoryId, Collection<Long> groupIds,
      Collection<Long> userIds);

  @Modifying
  @Query("DELETE FROM GroupUser ug WHERE ug.userId in (?1)")
  void deleteAllByUserIdIn(Set<Long> userIds);

  @Modifying
  @Query("DELETE FROM GroupUser ud WHERE ud.groupId = ?1 AND ud.userId in ?2")
  void deleteByGroupIdAndUserIdIn(Long groupId, Set<Long> userIds);

  @Modifying
  @Query("DELETE FROM GroupUser ud WHERE ud.groupId IN (?1) AND ud.userId = ?2")
  void deleteByGroupIdInAndUserId(Set<Long> groupIds, Long userId);

  @Modifying
  @Query("DELETE FROM GroupUser ug WHERE ug.groupId IN (?1)")
  void deleteAllByGroupIdIn(Set<Long> groupIds);

  @Modifying
  @Query("DELETE FROM GroupUser ug WHERE ug.tenantId IN (?1)")
  void deleteByTenantId(Set<Long> tenantIds);

}
