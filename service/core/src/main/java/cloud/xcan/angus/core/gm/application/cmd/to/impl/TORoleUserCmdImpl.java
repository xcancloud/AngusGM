package cloud.xcan.angus.core.gm.application.cmd.to.impl;

import cloud.xcan.angus.api.commonlink.to.TORoleUser;
import cloud.xcan.angus.api.commonlink.to.TORoleUserRepo;
import cloud.xcan.angus.api.manager.UserManager;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.to.TORoleUserCmd;
import cloud.xcan.angus.core.gm.application.query.to.TORoleQuery;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * Implementation of tenant operation role-user command operations.
 * </p>
 * <p>
 * Manages role-user authorization relationships including assignment and removal.
 * Provides comprehensive role-user management with proper validation.
 * </p>
 * <p>
 * Supports both user-centric and role-centric authorization management
 * with proper cleanup and validation.
 * </p>
 */
@Biz
public class TORoleUserCmdImpl extends CommCmd<TORoleUser, Long> implements TORoleUserCmd {

  @Resource
  private TORoleUserRepo toRoleUserRepo;
  @Resource
  private TORoleQuery toRoleQuery;
  @Resource
  private UserManager userManager;

  /**
   * <p>
   * Assigns roles to a specific user.
   * </p>
   * <p>
   * Validates user and role existence, removes existing authorizations,
   * and creates new role-user assignments.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void userRoleAuth(Long userId, Set<Long> roleIds) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        // Verify user exists
        userManager.checkValidAndFind(userId);

        // Verify roles exist
        toRoleQuery.checkAndFind(roleIds, true);
      }

      @Override
      protected Void process() {
        // Remove existing authorizations
        toRoleUserRepo.deleteByUserIdAndRoleIdIn(userId, roleIds);
        // Create new authorizations
        List<TORoleUser> roleUsers = roleIds.stream()
            .map(roleId -> new TORoleUser().setId(uidGenerator.getUID())
                .setUserId(userId).setToRoleId(roleId))
            .collect(Collectors.toList());
        toRoleUserRepo.batchInsert(roleUsers);
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Removes specific roles from a user.
   * </p>
   * <p>
   * Deletes role-user assignments for the specified user and roles.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void userRoleDelete(Long userId, HashSet<Long> roleIds) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        toRoleUserRepo.deleteByUserIdAndRoleIdIn(userId, roleIds);
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Assigns a specific role to multiple users.
   * </p>
   * <p>
   * Validates users and role existence, removes existing authorizations,
   * and creates new role-user assignments.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void roleUserAuth(Long roleId, HashSet<Long> userIds) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        // Verify users exist
        userManager.checkValidAndFind(userIds);

        // Verify role exists
        toRoleQuery.checkAndFind(roleId, true);
      }

      @Override
      protected Void process() {
        // Remove existing authorizations
        toRoleUserRepo.deleteByUserIdInAndRoleId(userIds, roleId);
        // Create new authorizations
        List<TORoleUser> roleUsers = userIds.stream()
            .map(userId -> new TORoleUser().setId(uidGenerator.getUID())
                .setUserId(userId).setToRoleId(roleId))
            .collect(Collectors.toList());
        toRoleUserRepo.batchInsert(roleUsers);
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Removes a specific role from multiple users.
   * </p>
   * <p>
   * Deletes role-user assignments for the specified role and users.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void roleUserDelete(Long roleId, HashSet<Long> userIds) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        toRoleUserRepo.deleteByUserIdInAndRoleId(userIds, roleId);
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<TORoleUser, Long> getRepository() {
    return this.toRoleUserRepo;
  }
}
