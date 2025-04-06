package cloud.xcan.angus.api.commonlink.group;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.jpa.repository.NameJoinRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("commonGroupRepo")
public interface GroupRepo extends NameJoinRepository<Group, Long>, BaseRepository<Group, Long> {

  @Override
  @Query(value = "SELECT * FROM group0 WHERE id IN ?1", nativeQuery = true)
  List<Group> findByIdIn(Collection<Long> ids);

  @Query(value = "SELECT u FROM Group u WHERE u.id = ?1 AND u.enabled = true")
  Optional<Group> findValidById(Long id);

  @Query(value = "SELECT u.id FROM group0 u WHERE u.id IN ?1", nativeQuery = true)
  List<Long> findIdsByIdIn(Collection<Long> ids);

  List<Group> findAllByTenantIdAndCodeIn(Long tenantId, Set<String> codes);

  @Query(value = "SELECT u.id FROM user0 u WHERE u.id not in (SELECT gu.user_id FROM group_user gu WHERE gu.group_id = ?1)", nativeQuery = true)
  Page<Long> selectUserIdNotInGroup(Long groupId, Pageable pageable);

  @Query(value = "SELECT u.id FROM group0 u WHERE u.directory_id = ?1", nativeQuery = true)
  Set<Long> findGroupIdsByDirectoryId(Long id);

  Page<Group> findAllByIdIn(Collection<Long> ids, Pageable page);

  Page<Group> findAllByIdNotIn(Collection<Long> ids, Pageable page);

  List<Group> findAllByTenantId(Long tenantId);

  List<Group> findByTenantIdAndDirectoryId(Long tenantId, Long directoryId);

  @Query(value = "SELECT id FROM Group")
  Page<Long> findAllIds(Pageable pageable);

  @Query(value =
      "SELECT * FROM group0 WHERE tenant_id = ?1 AND enabled = 1 AND case when CONCAT(?2,'') IS NOT NULL then id IN (?2) else 1=1 end"
          + " AND case when CONCAT(?3,'') IS NOT NULL then id IN (?3) else 1=1 end AND case when CONCAT(?4,'') IS NOT NULL then name like CONCAT('%',?4,'%') else 1=1 end",
      countQuery =
          "SELECT COUNT(*) FROM group0 WHERE tenant_id = ?1 AND enabled = 1 AND case when CONCAT(?2,'') IS NOT NULL then id IN (?2) else 1=1 end"
              + " AND case when CONCAT(?3,'') IS NOT NULL then id IN (?3) else 1=1 end AND case when CONCAT(?4,'') IS NOT NULL then name like CONCAT('%',?4,'%') else 1=1 end",
      nativeQuery = true)
  Page<Group> findValidByOrgIdAndIdAndName(Long tenantId, Collection<?> orgIds,
      Collection<?> idsFilter, String nameFilter, Pageable page);

  @Query(value =
      "SELECT * FROM group0 WHERE tenant_id = ?1 AND enabled = 1"
          + " AND case when CONCAT(?2,'') IS NOT NULL then id IN (?2) else 1=1 end AND case when CONCAT(?3,'') IS NOT NULL then name like CONCAT('%',?3,'%') else 1=1 end",
      countQuery =
          "SELECT COUNT(*) FROM group0 WHERE tenant_id = ?1 AND enabled = 1"
              + " AND case when CONCAT(?2,'') IS NOT NULL then id IN (?2) else 1=1 end AND case when CONCAT(?3,'') IS NOT NULL then name like CONCAT('%',?3,'%') else 1=1 end",
      nativeQuery = true)
  Page<Group> findAllValidByIdAndName(Long tenantId, Collection<?> idsFilter, String nameFilter,
      Pageable page);

  @Query(value =
      "SELECT * FROM group0 WHERE tenant_id = ?1 AND enabled = 1 AND case when CONCAT(?2,'') IS NOT NULL then id NOT IN (?2) else 1=1 end"
          + " AND case when CONCAT(?3,'') IS NOT NULL then id IN (?3) else 1=1 end AND case when CONCAT(?4,'') IS NOT NULL then name like CONCAT('%',?4,'%') else 1=1 end",
      countQuery =
          "SELECT COUNT(*) FROM group0 WHERE tenant_id = ?1 AND enabled = 1 AND case when CONCAT(?2,'') IS NOT NULL then id NOT IN (?2) else 1=1 end"
              + " AND case when CONCAT(?3,'') IS NOT NULL then id IN (?3) else 1=1 end AND case when CONCAT(?4,'') IS NOT NULL then name like CONCAT('%',?4,'%') else 1=1 end",
      nativeQuery = true)
  Page<Group> findValidByOrgIdNotAndIdAndName(Long tenantId, Collection<?> orgIds,
      Collection<?> idsFilter, String nameFilter, Pageable page);

  Integer countByTenantId(Long tenantId);

  @Query(value = "SELECT u FROM Group u WHERE u.name like %?1% ")
  List<Group> findByNameLike(String name);

  List<Group> findAllByIdInAndEnabled(Collection<Long> ids, Boolean enabled);

  @Transactional(rollbackFor = Exception.class) /* Fix:: javax.persistence.TransactionRequiredException: Executing an update/delete query */
  @Modifying
  @Query(value = "UPDATE group0 t set t.directory_id = null WHERE t.id IN (?1)", nativeQuery = true)
  int updateDirectoryEmptyByGroupIdIn(Set<Long> groupIds);

  @Transactional(rollbackFor = Exception.class) /* Fix:: javax.persistence.TransactionRequiredException: Executing an update/delete query */
  @Modifying
  @Query(value = "UPDATE group0 t set t.directory_id = null WHERE t.directory_id = ?1", nativeQuery = true)
  int updateDirectoryEmptyByDirectoryId(Long directoryId);

  @Modifying
  @Query(value = "DELETE FROM group0 WHERE id IN (?1)", nativeQuery = true)
  void deleteByIdIn(Collection<Long> ids);

  @Modifying
  @Query(value = "DELETE FROM group0 WHERE directory_id = ?1 AND source = ?2", nativeQuery = true)
  void deleteByDirectoryIdAndSource(Long id, String value);

}
