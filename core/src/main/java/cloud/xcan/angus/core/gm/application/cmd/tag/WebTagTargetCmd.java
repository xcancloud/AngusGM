package cloud.xcan.angus.core.gm.application.cmd.tag;

import cloud.xcan.angus.api.commonlink.app.tag.WebTagTarget;
import cloud.xcan.angus.api.commonlink.app.tag.WebTagTargetType;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


public interface WebTagTargetCmd {

  List<IdKey<Long, Object>> tagTargetAdd(Long tagId, List<WebTagTarget> tagTargets);

  void tagTargetDelete(Long tagId, HashSet<Long> targetIds);

  List<IdKey<Long, Object>> appTagAdd(Long appId, LinkedHashSet<Long> tagIds);

  void appTagReplace(Long appId, LinkedHashSet<Long> tagIds);

  void appTagDelete(Long appId, HashSet<Long> tagIds);

  List<IdKey<Long, Object>> funcTagAdd(Long appId, LinkedHashSet<Long> tagIds);

  void funcTagReplace(Long appId, LinkedHashSet<Long> tagIds);

  void funcTagDelete(Long appId, HashSet<Long> tagIds);

  void add(List<WebTagTarget> tagTargets);

  void tag(WebTagTargetType targetType, Long targetId, Set<Long> tagIds);

  void delete(Collection<Long> targetIds);

  void deleteAllByTarget(WebTagTargetType targetType, Collection<Long> targetIds);

  void deleteAllByTargetNot(WebTagTargetType targetType, Collection<Long> targetIds);
}
