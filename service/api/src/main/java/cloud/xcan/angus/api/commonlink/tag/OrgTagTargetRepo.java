package cloud.xcan.angus.api.commonlink.tag;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository("commonTagTargetRepo")
public interface OrgTagTargetRepo extends BaseRepository<OrgTagTarget, Long> {

  long countByTenantIdAndTargetId(Long optTenantId, Long orgId);

  List<OrgTagTarget> findAllByTargetTypeAndTargetId(OrgTargetType targetType, Long targetId);

  Set<OrgTagTarget> findByTagIdInAndTargetTypeAndTargetId(Collection<Long> tagIds,
      OrgTargetType targetType, Long targetId);

  Set<OrgTagTarget> findByTagIdAndTargetTypeAndTargetIdIn(Long tagId, OrgTargetType targetType,
      Collection<Long> targetIds);

  Set<Long> findByTagIdAndTargetIdIn(Long tagId, Collection<Long> targetIds);

  @Modifying
  @Query("DELETE FROM OrgTagTarget WHERE targetId IN (?1)")
  void deleteByTargetIdIn(Set<Long> targetIds);

  @Modifying
  @Query("DELETE FROM OrgTagTarget WHERE targetType =?1 AND targetId in ?2")
  void deleteByTargetTypeAndTargetIdIn(OrgTargetType targetType, Collection<Long> targetIds);

  @Modifying
  @Query("DELETE FROM OrgTagTarget WHERE tagId IN (?1)")
  void deleteByTagIdIn(Set<Long> tagIds);

  @Modifying
  @Query("DELETE FROM OrgTagTarget WHERE tagId = ?1 AND targetId IN ?2")
  void deleteByTagIdAndTargetIdIn(Long tagId, Collection<Long> targetIds);

  @Modifying
  @Query("DELETE FROM OrgTagTarget WHERE tagId IN (?1) AND targetId = ?2")
  void deleteByTagIdInAndTargetId(Collection<Long> tagIds, Long targetIds);

  @Modifying
  @Query("DELETE FROM OrgTagTarget WHERE tenantId = ?1")
  void deleteAllByTenantId(Set<Long> tenantIds);

}
