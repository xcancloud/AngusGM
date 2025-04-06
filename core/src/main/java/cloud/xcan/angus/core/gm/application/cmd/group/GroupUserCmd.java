package cloud.xcan.angus.core.gm.application.cmd.group;

import cloud.xcan.angus.api.commonlink.user.group.GroupUser;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public interface GroupUserCmd {

  List<IdKey<Long, Object>> groupAdd(Long userId, LinkedHashSet<Long> groupIds);

  void groupReplace(Long userId, LinkedHashSet<Long> groupIds);

  void groupDelete(Long userId, HashSet<Long> groupIds);

  List<IdKey<Long, Object>> userAdd(Long groupId, List<GroupUser> groupUsers);

  void userDelete(Long groupId, Set<Long> userIds);

  void deleteByUserId(Set<Long> userIds);

  void deleteByTenantId(Set<Long> tenantIds);

  List<IdKey<Long, Object>> add(List<GroupUser> groupUsers);

  List<IdKey<Long, Object>> add0(List<GroupUser> groupUsers);

  void delete0(Long directoryId, List<GroupUser> groupUsers);

  void deleteAllByGroupId(Set<Long> ids);

}
