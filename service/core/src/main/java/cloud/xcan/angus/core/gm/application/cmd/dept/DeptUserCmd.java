package cloud.xcan.angus.core.gm.application.cmd.dept;

import cloud.xcan.angus.api.commonlink.user.dept.DeptUser;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public interface DeptUserCmd {

  List<IdKey<Long, Object>> deptAdd(Long userId, List<DeptUser> deptUsers);

  void deptReplace(Long userId, List<DeptUser> deptUsers);

  void deptDelete(Long userId, HashSet<Long> deptIds);

  List<IdKey<Long, Object>> userAdd(Long deptId, List<DeptUser> deptUsers);

  void userDelete(Long userId, HashSet<Long> deptIds);

  void headReplace(Long deptId, Long userId, Boolean head);

  List<IdKey<Long, Object>> add0(List<DeptUser> deptUsers);

  void deleteAllByDeptId(Collection<Long> deptIds);

  void deleteByUserId(Set<Long> userIds);

  void deleteByTenantId(Set<Long> tenantIds);

}
