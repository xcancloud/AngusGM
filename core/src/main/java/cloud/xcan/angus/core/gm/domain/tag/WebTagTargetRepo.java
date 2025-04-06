package cloud.xcan.angus.core.gm.domain.tag;

import cloud.xcan.angus.api.commonlink.app.tag.WebTagTarget;
import cloud.xcan.angus.api.commonlink.app.tag.WebTagTargetType;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


public interface WebTagTargetRepo extends BaseRepository<WebTagTarget, Long> {

  List<WebTagTarget> findByTargetTypeAndTargetId(WebTagTargetType targetType, Long targetId);

  List<WebTagTarget> findAllByTargetTypeAndTargetIdIn(WebTagTargetType targetType,
      Collection<Long> targetIds);

  List<WebTagTarget> findAllByTargetId(Long targetId);

  List<WebTagTarget> findAllByTargetIdIn(Collection<Long> targetIds);

  Set<WebTagTarget> findByTagIdInAndTargetTypeAndTargetId(Collection<Long> tagIds,
      WebTagTargetType targetType, Long appId);

  Set<WebTagTarget> findByTagIdAndTargetTypeAndTargetIdIn(Long tagId, WebTagTargetType targetType,
      List<Long> appIds);

  Set<WebTagTarget> findByTagIdAndTargetTypeNotAndTargetIdIn(Long tagId,
      WebTagTargetType targetType, Collection<Long> funcIds);

  Set<WebTagTarget> findByTagIdInAndTargetTypeNotAndTargetId(Collection<Long> tagIds,
      WebTagTargetType targetType, Long funcId);

  @Modifying
  @Query(value = "DELETE FROM web_tag_target WHERE tag_id IN ?1", nativeQuery = true)
  void deleteByTagIdIn(HashSet<Long> tagIds);

  @Modifying
  @Query(value = "DELETE FROM web_tag_target WHERE target_id IN ?1", nativeQuery = true)
  void deleteByTargetIdIn(Collection<Long> targetIds);

  @Modifying
  @Query(value = "DELETE FROM web_tag_target WHERE target_type = ?1 AND target_id IN ?2", nativeQuery = true)
  void deleteAllByTargetTypeAndTargetIdIn(WebTagTargetType targetType, Collection<Long> targetIds);

  @Modifying
  @Query("DELETE FROM WebTagTarget WHERE tagId IN ?1 AND targetId = ?2")
  void deleteByTagIdInAndTargetId(Collection<Long> tagIds, Long targetId);

  @Modifying
  @Query("DELETE FROM WebTagTarget WHERE tagId = ?1 AND targetId IN ?2")
  void deleteByTagIdAndTargetIdIn(Long tagId, Collection<Long> targetIds);

  @Modifying
  @Query("DELETE FROM WebTagTarget WHERE targetType <> ?1 AND targetId IN ?2")
  void deleteAllByTargetTypeNotAndTargetIdIn(WebTagTargetType targetType,
      Collection<Long> targetIds);
}
