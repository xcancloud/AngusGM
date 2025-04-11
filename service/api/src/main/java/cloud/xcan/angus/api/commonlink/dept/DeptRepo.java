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

  /**
   * <pre>
   * * SQL Exception:: 1241 - Operand should contain 1 column(s), Time: 0.023000s
   * ----------------------------------------------------
   * SELECT	* FROM	dept WHERE tenant_id = 1 AND
   *  CASE
   * 	 WHEN ( 129959128339579154, 133378265904578570, 132224012771131413, 125935710350868495 ) IS NOT NULL
   * 	  THEN id IN ( 129959128339579154, 133378265904578570, 132224012771131413, 125935710350868495 ) ELSE 1 = 1
   *  END
   *
   * *** Fix:: use case when ?2 -> case when CONCAT(?2,'') ***
   * SELECT	* FROM	dept WHERE tenant_id = 1 AND
   *  CASE
   * 	 WHEN '129959128339579154, 133378265904578570, 132224012771131413, 125935710350868495' IS NOT NULL
   * 	 THEN id IN ( 129959128339579154, 133378265904578570, 132224012771131413, 125935710350868495 ) ELSE 1 = 1
   *  END
   *
   * SQL Exception:: 1064 - You have an error in your SQL syntax when `Collection is null`
   * ----------------------------------------------------
   * SELECT	* FROM	dept WHERE tenant_id = 1 AND
   *  CASE WHEN CONCAT(null,'') IS NOT NULL then id IN null else 1=1
   *  END
   *
   * *** Fix:: case when CONCAT(?2,'') IS NOT NULL then id IN ?2 -> case when CONCAT(?2,'') IS NOT NULL then id IN (?2) ***
   * SELECT	* FROM	dept WHERE tenant_id = 1 AND
   *  CASE WHEN CONCAT(null,'') IS NOT NULL then id IN (null) else 1=1
   *  END
   * <pre/>
   */
  @Query(value =
      "SELECT * FROM dept WHERE tenant_id = ?1 AND case when CONCAT(?2,'') IS NOT NULL then id IN (?2) else 1=1 end"
          + " AND case when CONCAT(?3,'') IS NOT NULL then id IN (?3) else 1=1 end AND case when CONCAT(?4,'') IS NOT NULL then name like CONCAT('%',?4,'%') else 1=1 end",
      countQuery =
          "SELECT COUNT(*) FROM dept WHERE tenant_id = ?1 AND case when CONCAT(?2,'') IS NOT NULL then id IN (?2) else 1=1 end"
              + " AND case when CONCAT(?3,'') IS NOT NULL then id IN (?3) else 1=1 end AND case when CONCAT(?4,'') IS NOT NULL then name like CONCAT('%',?4,'%') else 1=1 end",
      nativeQuery = true)
  Page<Dept> findByOrgIdAndIdAndName(Long tenantId, Collection<?> orgIds, Collection<?> idsFilter,
      String nameFilter, Pageable page);

  @Query(value =
      "SELECT * FROM dept WHERE tenant_id = ?1 "
          + " AND case when CONCAT(?2,'') IS NOT NULL then id IN CONCAT(?2,'') else 1=1 end AND case when CONCAT(?3,'') IS NOT NULL then name like CONCAT('%',?3,'%') else 1=1 end",
      countQuery =
          "SELECT COUNT(*) FROM dept WHERE tenant_id = ?1"
              + " AND case when CONCAT(?2,'') IS NOT NULL then id IN CONCAT(?2,'') else 1=1 end AND case when CONCAT(?3,'') IS NOT NULL then name like CONCAT('%',?3,'%') else 1=1 end",
      nativeQuery = true)
  Page<Dept> findAllByIdAndName(Long tenantId, Collection<?> idsFilter, String nameFilter,
      Pageable page);

  @Query(value =
      "SELECT * FROM dept WHERE tenant_id = ?1 AND case when CONCAT(?2,'') IS NOT NULL then id NOT IN (?2) else 1=1 end"
          + " AND case when CONCAT(?3,'') IS NOT NULL then id IN (?3) else 1=1 end AND case when CONCAT(?4,'') IS NOT NULL then name like CONCAT('%',?4,'%') else 1=1 end",
      countQuery =
          "SELECT COUNT(*) FROM dept WHERE tenant_id = ?1 AND case when CONCAT(?2,'') IS NOT NULL then id NOT IN (?2) else 1=1 end"
              + " AND case when CONCAT(?3,'') IS NOT NULL then id IN (?3) else 1=1 end AND case when CONCAT(?4,'') IS NOT NULL then name like CONCAT('%',?4,'%') else 1=1 end",
      nativeQuery = true)
  Page<Dept> findByOrgIdNotAndIdAndName(Long tenantId, Collection<?> orgIds,
      Collection<?> idsFilter, String nameFilter, Pageable page);

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
