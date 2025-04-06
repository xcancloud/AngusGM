package cloud.xcan.angus.api.commonlink.to;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository("commonTORoleUserRepo")
public interface TORoleUserRepo extends BaseRepository<TORoleUser, Long> {

  List<TORoleUser> findAllByUserIdIn(Collection<Long> userIds);

  List<TORoleUser> findAllByToRoleIdIn(Collection<Long> roleIds);

  @Query(value = "SELECT pu.* FROM to_role p INNER JOIN to_role_user pu "
      + "ON p.id = pu.to_role_id AND p.`code` = ?1", nativeQuery = true)
  List<TORoleUser> findAllByRoleCode(String roleCode);

  @Query(value = "SELECT pu.* FROM to_role p INNER JOIN to_role_user pu "
      + "ON p.id = pu.to_role_id AND p.`code` IN ?1", nativeQuery = true)
  List<TORoleUser> findAllByRoleCodeIn(Collection<String> roleCodes);

  @Query(value =
      "SELECT DISTINCT tp.code FROM to_role tp, to_role_user tpu WHERE "
          + " tp.id = tpu.to_role_id AND tpu.user_id = ?1", nativeQuery = true)
  List<String> findUserGrantRoles(Long userId);

  @Query(value =
      "SELECT STRAIGHT_JOIN u.mobile FROM to_role tp INNER JOIN to_role_user tpu ON tp.id = tpu.to_role_id AND tp.id IN ?1 "
          + "INNER JOIN user u ON tpu.user_id = u.id AND u.deleted = 0 "
          + "AND u.enabled =1 ORDER BY u.id ASC LIMIT ?2,?3", nativeQuery = true)
  List<String> findValidMobileByRoleIds(List<Long> roleIds, int offset, int size);

  @Query(value =
      "SELECT STRAIGHT_JOIN u.mobile FROM to_role tp INNER JOIN to_role_user tpu ON tp.id = tpu.to_role_id AND tp.code IN ?1 "
          + "INNER JOIN user u ON tpu.user_id = u.id  AND u.deleted = 0 AND u.enabled =1 ORDER BY u.id ASC LIMIT ?2,?3", nativeQuery = true)
  List<String> findValidMobileByRoleCodes(List<String> roleCodes, int offset, int size);

  @Query(value =
      "SELECT STRAIGHT_JOIN u.email FROM to_role tp INNER JOIN to_role_user tpu ON tp.id = tpu.to_role_id AND tp.id IN ?1 "
          + "INNER JOIN user u ON tpu.user_id = u.id AND u.deleted = 0 "
          + "AND u.enabled =1 ORDER BY u.id ASC LIMIT ?2,?3", nativeQuery = true)
  List<String> findValidEmailByRoleIds(List<Long> roleIds, int offset, int size);

  @Query(value =
      "SELECT STRAIGHT_JOIN u.email FROM to_role tp INNER JOIN to_role_user tpu ON tp.id = tpu.to_role_id AND tp.code IN ?1 "
          + "INNER JOIN user u ON tpu.user_id = u.id AND u.deleted = 0 AND u.enabled =1 ORDER BY u.id ASC LIMIT ?2,?3", nativeQuery = true)
  List<String> findValidEmailByRoleCodes(List<String> toRoleCodes, int offset, int size);

  @Modifying
  @Query(value = "DELETE FROM to_role_user WHERE to_role_id IN ?1", nativeQuery = true)
  void deleteByToRoleIdIn(Collection<Long> toRoleIds);

  @Modifying
  @Query(value = "DELETE FROM to_role_user WHERE user_id IN ?1", nativeQuery = true)
  void deleteByUserIdIn(Collection<Long> userIds);

  @Modifying
  @Query(value = "DELETE FROM to_role_user WHERE user_id = ?1 AND to_role_id IN ?2", nativeQuery = true)
  void deleteByUserIdAndRoleIdIn(Long userId, Collection<Long> toRoleIds);

  @Modifying
  @Query(value = "DELETE FROM to_role_user WHERE user_id IN ?1 AND to_role_id = ?2", nativeQuery = true)
  void deleteByUserIdInAndRoleId(Collection<Long> userIds, Long toRoleId);

}
