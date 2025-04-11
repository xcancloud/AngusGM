package cloud.xcan.angus.core.gm.application.cmd.tag;

import cloud.xcan.angus.api.commonlink.tag.OrgTagTarget;
import cloud.xcan.angus.api.commonlink.tag.OrgTargetType;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public interface OrgTagTargetCmd {

  List<IdKey<Long, Object>> tagTargetAdd(Long tagId, List<OrgTagTarget> tagTargets);

  void tagTargetDelete(Long tagId, HashSet<Long> targetIds);

  List<IdKey<Long, Object>> userTagAdd(Long userId, LinkedHashSet<Long> tagIds);

  void userTagReplace(Long userId, LinkedHashSet<Long> tagIds);

  void userTagDelete(Long userId, HashSet<Long> tagIds);

  List<IdKey<Long, Object>> deptTagAdd(Long deptId, LinkedHashSet<Long> tagIds);

  void deptTagReplace(Long id, LinkedHashSet<Long> ids);

  void deptTagDelete(Long deptId, HashSet<Long> tagIds);

  List<IdKey<Long, Object>> groupTagAdd(Long groupId, LinkedHashSet<Long> tagIds);

  void groupTagReplace(Long groupId, LinkedHashSet<Long> userIds);

  void groupTagDelete(Long groupId, HashSet<Long> tagIds);

  void add(List<OrgTagTarget> tagTargets);

  void deleteAllByTarget(OrgTargetType targetType, Collection<Long> targetIds);

  void deleteAllByTenantId(Set<Long> tenantIds);

}
