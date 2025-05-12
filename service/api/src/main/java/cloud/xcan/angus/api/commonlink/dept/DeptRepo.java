package cloud.xcan.angus.api.commonlink.dept;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.jpa.repository.NameJoinRepository;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository("commonDeptRepo")
public interface DeptRepo extends NameJoinRepository<Dept, Long>, BaseRepository<Dept, Long> {

  @Override
  @Query(value = "SELECT * FROM dept WHERE id IN ?1", nativeQuery = true)
  List<Dept> findByIdIn(Collection<Long> ids);

  @Query(value = "SELECT id FROM dept WHERE id IN ?1", nativeQuery = true)
  List<Long> findIdsByIdIn(Collection<Long> ids);

  Integer countByTenantId(Long tenantId);

  List<Dept> findAllByTenantIdAndCodeIn(Long tenantId, Set<String> codes);

  Set<Long> findByTenantIdAndPidIn(Long tenantId, Set<Long> pids);

  List<Dept> findByTenantIdAndIdIn(Long tenantId, List<?> ids);

  Page<Dept> findAllByIdIn(Collection<Long> ids, Pageable page);

  Page<Dept> findAllByIdNotIn(Collection<Long> ids, Pageable page);

  @Query(value = "SELECT id FROM Dept")
  Page<Long> findAllIds(Pageable pageable);

  @Query(value = "SELECT d FROM Dept d WHERE d.name like %?1% ")
  List<Dept> findByNameLike(String name);

  @Query(value = "SELECT id FROM dept WHERE parent_like_id LIKE CONCAT(?1,'%')", nativeQuery = true)
  List<Long> findIdByParentLikeId(String subParentLikeId);

  @Query(value = "SELECT DISTINCT pid FROM dept WHERE pid IN (?1)", nativeQuery = true)
  Set<Long> findPidBySubPid(Set<Long> pids);

  @Query(value = "SELECT * FROM dept WHERE tenant_id = ?1 AND parent_like_id LIKE CONCAT(?2,'%')", nativeQuery = true)
  List<Dept> findSubDeptsByParentLikeId(Long optTenantId, String subParentLikeId);

  @Modifying
  @Query(value = "DELETE FROM dept WHERE id IN (?1)", nativeQuery = true)
  void deleteByIdIn(List<Long> ids);

  @Modifying
  @Query(value =
      "UPDATE dept SET level = level + ?1, parent_like_id = REPLACE(parent_like_id, ?2, ?3) "
          + " WHERE parent_like_id LIKE CONCAT(?2,'%')", nativeQuery = true)
  void updateSubParentByOldParentLikeId(int newDiffLevel, String oldSubParentLikeId,
      String newSubParentLikeId);

}
