package cloud.xcan.angus.core.gm.interfaces.to.facade.internal;

import cloud.xcan.angus.core.gm.application.cmd.to.TORoleUserCmd;
import cloud.xcan.angus.core.gm.interfaces.to.facade.TOUserRoleFacade;
import jakarta.annotation.Resource;
import java.util.HashSet;
import org.springframework.stereotype.Component;


@Component
public class TOUserRoleFacadeImpl implements TOUserRoleFacade {

  @Resource
  private TORoleUserCmd toRoleUserCmd;

  @Override
  public void userRoleAuth(Long userId, HashSet<Long> roleIds) {
    toRoleUserCmd.userRoleAuth(userId, roleIds);
  }

  @Override
  public void userRoleDelete(Long userId, HashSet<Long> roleIds) {
    toRoleUserCmd.userRoleDelete(userId, roleIds);
  }

  @Override
  public void roleUserAuth(Long roleId, HashSet<Long> userIds) {
    toRoleUserCmd.roleUserAuth(roleId, userIds);
  }

  @Override
  public void roleUserDelete(Long roleId, HashSet<Long> userIds) {
    toRoleUserCmd.roleUserDelete(roleId, userIds);
  }

}
