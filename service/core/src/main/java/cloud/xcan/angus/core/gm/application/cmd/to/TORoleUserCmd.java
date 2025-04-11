package cloud.xcan.angus.core.gm.application.cmd.to;

import java.util.HashSet;
import java.util.Set;

public interface TORoleUserCmd {

  void userRoleAuth(Long userId, Set<Long> roleIds);

  void userRoleDelete(Long userId, HashSet<Long> roleIds);

  void roleUserAuth(Long roleId, HashSet<Long> userIds);

  void roleUserDelete(Long roleId, HashSet<Long> userIds);
}
