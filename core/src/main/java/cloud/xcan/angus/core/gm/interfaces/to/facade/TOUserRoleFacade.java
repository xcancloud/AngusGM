package cloud.xcan.angus.core.gm.interfaces.to.facade;


import java.util.HashSet;

public interface TOUserRoleFacade {

  void userRoleAuth(Long userId, HashSet<Long> roleIds);

  void userRoleDelete(Long userId, HashSet<Long> roleIds);

  void roleUserAuth(Long roleId, HashSet<Long> userIds);

  void roleUserDelete(Long roleId, HashSet<Long> userIds);

}
