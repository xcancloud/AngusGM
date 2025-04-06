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


@Biz
public class TORoleUserCmdImpl extends CommCmd<TORoleUser, Long> implements TORoleUserCmd {

  @Resource
  private TORoleUserRepo toRoleUserRepo;

  @Resource
  private TORoleQuery toRoleQuery;

  @Resource
  private UserManager userManager;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void userRoleAuth(Long userId, Set<Long> roleIds) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        // Check the operation user existed
        userManager.checkValidAndFind(userId);

        // Check the operation roles existed
        toRoleQuery.checkAndFind(roleIds, true);
      }

      @Override
      protected Void process() {
        // Delete existed authorizations
        toRoleUserRepo.deleteByUserIdAndRoleIdIn(userId, roleIds);
        // Save authorizations
        List<TORoleUser> roleUsers = roleIds.stream()
            .map(roleId -> new TORoleUser().setId(uidGenerator.getUID())
                .setUserId(userId).setToRoleId(roleId))
            .collect(Collectors.toList());
        toRoleUserRepo.batchInsert(roleUsers);
        return null;
      }
    }.execute();
  }

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

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void roleUserAuth(Long roleId, HashSet<Long> userIds) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        // Check the operation user existed
        userManager.checkValidAndFind(userIds);

        // Check the operation roles existed
        toRoleQuery.checkAndFind(roleId, true);
      }

      @Override
      protected Void process() {
        // Delete existed authorizations
        toRoleUserRepo.deleteByUserIdInAndRoleId(userIds, roleId);
        // Save authorizations
        List<TORoleUser> roleUsers = userIds.stream()
            .map(userId -> new TORoleUser().setId(uidGenerator.getUID())
                .setUserId(userId).setToRoleId(roleId))
            .collect(Collectors.toList());
        toRoleUserRepo.batchInsert(roleUsers);
        return null;
      }
    }.execute();
  }

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
