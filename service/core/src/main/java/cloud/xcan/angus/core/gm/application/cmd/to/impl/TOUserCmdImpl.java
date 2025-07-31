package cloud.xcan.angus.core.gm.application.cmd.to.impl;

import cloud.xcan.angus.api.commonlink.authuser.AuthUserRepo;
import cloud.xcan.angus.api.commonlink.to.TORoleUserRepo;
import cloud.xcan.angus.api.commonlink.to.TOUser;
import cloud.xcan.angus.api.commonlink.to.TOUserRepo;
import cloud.xcan.angus.api.manager.UserManager;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.to.TOUserCmd;
import cloud.xcan.angus.core.gm.application.query.to.TOUserQuery;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * Implementation of tenant operation user command operations.
 * </p>
 * <p>
 * Manages tenant operation user lifecycle including creation and deletion.
 * Provides user management with OAuth2 integration and role association cleanup.
 * </p>
 * <p>
 * Supports tenant operation user creation with validation and proper
 * cleanup of associated roles and OAuth2 user flags.
 * </p>
 */
@Biz
public class TOUserCmdImpl extends CommCmd<TOUser, Long> implements TOUserCmd {

  @Resource
  private TOUserRepo toUserRepo;
  @Resource
  private TOUserQuery toUserQuery;
  @Resource
  private AuthUserRepo authUserRepo;
  @Resource
  private TORoleUserRepo toRoleUserRepo;
  @Resource
  private UserManager userManager;

  /**
   * <p>
   * Creates tenant operation users with validation.
   * </p>
   * <p>
   * Validates user existence and checks for duplicate operation users.
   * Updates OAuth2 user flags and creates tenant operation user records.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> add(List<TOUser> toUsers) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      Set<Long> userIds;

      @Override
      protected void checkParams() {
        // Verify users exist
        userIds = toUsers.stream().map(TOUser::getUserId).collect(Collectors.toSet());
        userManager.checkValidAndFind(userIds);
        // Verify operation users don't already exist
        toUserQuery.checkExists(userIds);
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        // Save tenant operation users
        List<IdKey<Long, Object>> idKeys = batchInsert(toUsers);
        // Update OAuth2 user TO user flag
        authUserRepo.updateToUserByIdIn(
            userIds.stream().map(Object::toString).collect(Collectors.toSet()), true);
        return idKeys;
      }
    }.execute();
  }

  /**
   * <p>
   * Deletes tenant operation users and associated data.
   * </p>
   * <p>
   * Removes tenant operation users, associated roles, and updates OAuth2 user flags.
   * Ensures proper cleanup of all related data.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(Set<Long> userIds) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        // Delete tenant operation users
        toUserRepo.deleteAllByUserIdIn(userIds);
        // Delete associated role assignments
        toRoleUserRepo.deleteByUserIdIn(userIds);
        // Update OAuth2 user TO user flag
        authUserRepo.updateToUserByIdIn(
            userIds.stream().map(Object::toString).collect(Collectors.toSet()), false);
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Deletes tenant operation users without OAuth2 flag update.
   * </p>
   * <p>
   * Removes tenant operation users and associated roles without updating
   * OAuth2 user flags. Used for internal cleanup operations.
   * </p>
   */
  @Override
  public void delete0(Set<Long> userIds) {
    // Delete tenant operation users
    toUserRepo.deleteAllByUserIdIn(userIds);
    // Delete associated role assignments
    toRoleUserRepo.deleteByUserIdIn(userIds);
  }

  @Override
  protected BaseRepository<TOUser, Long> getRepository() {
    return this.toUserRepo;
  }
}
