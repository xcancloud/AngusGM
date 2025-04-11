package cloud.xcan.angus.core.gm.application.cmd.user;

import cloud.xcan.angus.api.commonlink.tag.OrgTagTarget;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.commonlink.user.dept.DeptUser;
import cloud.xcan.angus.api.commonlink.user.group.GroupUser;
import cloud.xcan.angus.api.enums.UserSource;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface UserCmd {

  IdKey<Long, Object> add(User user, List<DeptUser> deptUsers, List<GroupUser> groupUsers,
      List<OrgTagTarget> userTags, UserSource userSource);

  IdKey<Long, Object> replace(User user, List<DeptUser> deptUsers, List<GroupUser> groupUsers,
      List<OrgTagTarget> userTags);

  void update(User user, List<DeptUser> deptUsers, List<GroupUser> groupUsers,
      List<OrgTagTarget> userTags);

  void delete(Set<Long> ids);

  void enabled(List<User> users);

  void locked(Long id, Boolean locked, LocalDateTime lockStartDate, LocalDateTime lockEndDate);

  void adminSet(Long userId, Boolean sysAdmin);

  void lockExpire(Set<Long> lockExpireUserIds);

  void unlockExpire(Set<Long> unlockExpireUserIds);

  void deleteDirectoryUsers(Set<Long> ids);

  void emptyDirectoryUsers(Set<Long> userIds);

  void emptyDirectoryUsers(Long directoryId);

  void clearMainDeptByDeptIdIn(Set<Long> deptIds);

  void clearMainDeptByUserIdAndDeptIdIn(Long userId, Set<Long> deptIds);

  void clearMainDeptByDeptIdAndUserIdIn(Long deptId, Set<Long> userIds);

  void deleteByTenant(Set<Long> ids);

  void deleteByDirectory(Long id, boolean deleteSync);

}
