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

  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> add(List<TOUser> toUsers) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      Set<Long> userIds;

      @Override
      protected void checkParams() {
        // Check the user existed
        userIds = toUsers.stream().map(TOUser::getUserId).collect(Collectors.toSet());
        userManager.checkValidAndFind(userIds);
        // Check the operation user existed
        toUserQuery.checkExists(userIds);
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        // Save tenant operation users
        List<IdKey<Long, Object>> idKeys = batchInsert(toUsers);
        // Update oauth2 user TO user flag
        authUserRepo.updateToUserByIdIn(
            userIds.stream().map(Object::toString).collect(Collectors.toSet()), true);
        return idKeys;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(Set<Long> userIds) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        toUserRepo.deleteAllByUserIdIn(userIds);
        toRoleUserRepo.deleteByUserIdIn(userIds);
        authUserRepo.updateToUserByIdIn(
            userIds.stream().map(Object::toString).collect(Collectors.toSet()), false);
        return null;
      }
    }.execute();
  }

  @Override
  public void delete0(Set<Long> userIds) {
    toUserRepo.deleteAllByUserIdIn(userIds);
    toRoleUserRepo.deleteByUserIdIn(userIds);
  }

  @Override
  protected BaseRepository<TOUser, Long> getRepository() {
    return this.toUserRepo;
  }
}
