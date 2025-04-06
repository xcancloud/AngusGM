package cloud.xcan.angus.core.gm.application.cmd.dept.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.gm.domain.UCCoreMessage.USER_MAIN_DEPT_NUM_ERROR;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.commonlink.user.UserRepo;
import cloud.xcan.angus.api.commonlink.user.dept.DeptUser;
import cloud.xcan.angus.api.commonlink.user.dept.DeptUserRepo;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.dept.DeptUserCmd;
import cloud.xcan.angus.core.gm.application.cmd.user.UserCmd;
import cloud.xcan.angus.core.gm.application.query.dept.DeptQuery;
import cloud.xcan.angus.core.gm.application.query.dept.DeptUserQuery;
import cloud.xcan.angus.core.gm.application.query.user.UserQuery;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@Biz
public class DeptUserCmdImpl extends CommCmd<DeptUser, Long> implements DeptUserCmd {

  @Resource
  private DeptUserRepo deptUserRepo;

  @Resource
  private UserRepo userRepo;

  @Resource
  private DeptQuery deptQuery;

  @Resource
  private UserCmd userCmd;

  @Resource
  private UserQuery userQuery;

  @Resource
  private DeptUserQuery userDeptQuery;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> deptAdd(Long userId, List<DeptUser> deptUsers) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      Set<Long> deptIds;

      @Override
      protected void checkParams() {
        // Check the user exists
        // deptIds = deptUsers.stream().map(DeptUser::getDeptId).collect(Collectors.toSet());
        userQuery.checkAndFind(userId);
        // Check the user dept quota
        userDeptQuery.checkUserDeptAppendQuota(getOptTenantId(), deptUsers.size(), userId);

        // Check the departments exists
        deptIds = deptUsers.stream().map(DeptUser::getDeptId).collect(Collectors.toSet());
        deptQuery.checkAndFind(deptIds);
        // Check the dept user quota
        for (Long deptId : deptIds) {
          userDeptQuery.checkDeptUserAppendQuota(getOptTenantId(), 1, deptId);
        }
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        // @DoInFuture("Set user mainDeptId")

        // Delete repeated in db
        deptUserRepo.deleteByUserIdAndDeptIdIn(userId, deptIds);
        return batchInsert(deptUsers);
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void deptReplace(Long userId, List<DeptUser> deptUsers) {
    new BizTemplate<Void>() {
      User userDb;

      @Override
      protected void checkParams() {
        userDb = userQuery.checkAndFind(userId);
        // Check the user dept quota
        userDeptQuery.checkUserDeptReplaceQuota(getOptTenantId(), deptUsers.size(), userId);
        for (DeptUser userDept : deptUsers) {
          // Check the dept user quota
          userDeptQuery.checkDeptUserAppendQuota(getOptTenantId(), 1, userDept.getDeptId());
        }
      }

      @Override
      protected Void process() {
        // Update user main dept
        DeptUser mainDept = deptUsers.stream().filter(DeptUser::getMainDept).findFirst()
            .orElse(deptUsers.get(0));
        userDb.setMainDeptId(mainDept.getDeptId());
        userRepo.save(userDb);

        // Clear empty
        deleteByUserId(Collections.singleton(userId));
        // Save new association
        if (isNotEmpty(deptUsers)) {
          add(deptUsers.stream().peek(deptUser -> deptUser.setTenantId(userDb.getTenantId()))
              .collect(Collectors.toList()));
        }
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void deptDelete(Long userId, HashSet<Long> deptIds) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        userQuery.checkAndFind(userId);
      }

      @Override
      protected Void process() {
        deptUserRepo.deleteByUserIdAndDeptIdIn(userId, deptIds);
        userCmd.clearMainDeptByUserIdAndDeptIdIn(userId, deptIds);
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> userAdd(Long deptId, List<DeptUser> deptUsers) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      Set<Long> userIds;

      @Override
      protected void checkParams() {
        // The user must and can only have one primary department
        // Bug:: ProtocolAssert.assertTrue(deptUsers.stream()
        //    .filter(DeptUser::getDeptHead).count() == 1, USER_MAIN_DEPT_NUM_ERROR);

        // Check depts exists
        // deptIds = deptUsers.stream().map(DeptUser::getDeptId).collect(Collectors.toSet());
        deptQuery.checkAndFind(deptId);
        // Check dept user quota
        userDeptQuery.checkDeptUserAppendQuota(getOptTenantId(), deptUsers.size(), deptId);

        // Check users exists
        userIds = deptUsers.stream().map(DeptUser::getUserId).collect(Collectors.toSet());
        userQuery.checkAndFind(userIds);
        // Check user dept quota
        for (Long userId : userIds) {
          userDeptQuery.checkUserDeptAppendQuota(getOptTenantId(), 1, userId);
        }
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        // @DoInFuture("Set user mainDeptId")

        // Delete repeated in db
        deptUserRepo.deleteByDeptIdInAndUserIdIn(Collections.singleton(deptId), userIds);
        return batchInsert(new HashSet<>(deptUsers));
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void userDelete(Long deptId, HashSet<Long> userIds) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        deptQuery.checkAndFind(deptId);
      }

      @Override
      protected Void process() {
        deptUserRepo.deleteByDeptIdAndUserIdIn(deptId, userIds);
        userCmd.clearMainDeptByDeptIdAndUserIdIn(deptId, userIds);
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void headReplace(Long deptId, Long userId, Boolean head) {
    new BizTemplate<Void>() {

      @Override
      protected void checkParams() {
        deptQuery.checkAndFind(deptId);
        userQuery.checkAndFind(userId);
      }

      @Override
      protected Void process() {
        // Cancel other department head
        if (head) {
          deptUserRepo.updateDeptHead(deptId, head);
        }
        // Update user department head status
        deptUserRepo.updateDeptHead(deptId, userId, head);
        userRepo.updateDeptHead(userId, head);
        return null;
      }
    }.execute();
  }

  @Override
  public List<IdKey<Long, Object>> add(List<DeptUser> deptUsers) {
    // The user must and can only have one primary department
    assertTrue(deptUsers.stream()
        .filter(DeptUser::getMainDept).count() == 1, USER_MAIN_DEPT_NUM_ERROR);
    // Check depts exists
    deptQuery.checkAndFind(deptUsers.stream().map(DeptUser::getDeptId)
        .collect(Collectors.toList()));
    return batchInsert(new HashSet<>(deptUsers));
  }

  @Override
  public void deleteByUserId(Set<Long> userIds) {
    deptUserRepo.deleteAllByUserIdIn(userIds);
  }

  @Override
  public void deleteByTenantId(Set<Long> tenantIds) {
    deptUserRepo.deleteAllByTenantIdIn(tenantIds);
  }

  @Override
  public void deleteAllByDeptId(Collection<Long> deptIds) {
    deptUserRepo.deleteAllByDeptIdIn(deptIds);
  }

  @Override
  protected BaseRepository<DeptUser, Long> getRepository() {
    return this.deptUserRepo;
  }
}
