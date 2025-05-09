package cloud.xcan.angus.core.gm.application.cmd.dept.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.gm.domain.UCCoreMessage.USER_MAIN_DEPT_NUM_ERROR;
import static cloud.xcan.angus.core.gm.domain.operation.OperationResourceType.DEPT;
import static cloud.xcan.angus.core.gm.domain.operation.OperationResourceType.USER;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.ADD_DEPT_USER;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.ADD_USER_DEPT;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.CANCEL_USER_MAIN_DEPT;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.DELETE_DEPT_USER;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.DELETE_USER_DEPT;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.SET_USER_MAIN_DEPT;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.UPDATE_USER_DEPT;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.dept.Dept;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.commonlink.user.UserRepo;
import cloud.xcan.angus.api.commonlink.user.dept.DeptUser;
import cloud.xcan.angus.api.commonlink.user.dept.DeptUserRepo;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.dept.DeptUserCmd;
import cloud.xcan.angus.core.gm.application.cmd.operation.OperationLogCmd;
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

  @Resource
  private OperationLogCmd operationLogCmd;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> deptAdd(Long userId, List<DeptUser> deptUsers) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      User userDb;
      Set<Long> deptIds;
      List<Dept> deptDb;

      @Override
      protected void checkParams() {
        // Check the user existed
        userDb = userQuery.checkAndFind(userId);
        // Check the user department quota
        userDeptQuery.checkUserDeptAppendQuota(getOptTenantId(), deptUsers.size(), userId);

        // Check the departments existed
        deptIds = deptUsers.stream().map(DeptUser::getDeptId).collect(Collectors.toSet());
        deptDb = deptQuery.checkAndFind(deptIds);
        // Check the department user quota
        for (Long deptId : deptIds) {
          userDeptQuery.checkDeptUserAppendQuota(getOptTenantId(), 1, deptId);
        }
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        // @DoInFuture("Set user mainDeptId")

        deptUserRepo.deleteByUserIdAndDeptIdIn(userId, deptIds);
        List<IdKey<Long, Object>> idKeys = batchInsert(deptUsers);

        operationLogCmd.add(USER, userDb, ADD_USER_DEPT,
            String.join(",", deptDb.stream().map(Dept::getName).toList()));
        return idKeys;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void deptReplace(Long userId, List<DeptUser> deptUsers) {
    new BizTemplate<Void>() {
      User userDb;
      List<Dept> deptDb;

      @Override
      protected void checkParams() {
        // Check the user existed
        userDb = userQuery.checkAndFind(userId);
        // Check the departments existed
        Set<Long> deptIds = deptUsers.stream().map(DeptUser::getDeptId).collect(Collectors.toSet());
        deptDb = deptQuery.checkAndFind(deptIds);

        // Check the user department quota
        userDeptQuery.checkUserDeptReplaceQuota(getOptTenantId(), deptUsers.size(), userId);
        for (DeptUser userDept : deptUsers) {
          // Check the department user quota
          userDeptQuery.checkDeptUserAppendQuota(getOptTenantId(), 1, userDept.getDeptId());
        }
      }

      @Override
      protected Void process() {
        // Update user main department
        DeptUser mainDept = deptUsers.stream().filter(DeptUser::getMainDept).findFirst()
            .orElse(deptUsers.get(0));
        userDb.setMainDeptId(mainDept.getDeptId());
        userRepo.save(userDb);

        // Clear empty
        deleteByUserId(Collections.singleton(userId));
        // Save new association
        if (isNotEmpty(deptUsers)) {
          add0(deptUsers.stream().peek(deptUser -> deptUser.setTenantId(userDb.getTenantId()))
              .collect(Collectors.toList()));
        }

        operationLogCmd.add(USER, userDb, UPDATE_USER_DEPT,
            String.join(",", deptDb.stream().map(Dept::getName).toList()));
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void deptDelete(Long userId, HashSet<Long> deptIds) {
    new BizTemplate<Void>() {
      User userDb;
      List<Dept> deptDb;

      @Override
      protected void checkParams() {
        // Check the user existed
        userDb = userQuery.checkAndFind(userId);
        // Check the departments existed
        deptDb = deptQuery.checkAndFind(deptIds);
      }

      @Override
      protected Void process() {
        deptUserRepo.deleteByUserIdAndDeptIdIn(userId, deptIds);
        userCmd.clearMainDeptByUserIdAndDeptIdIn(userId, deptIds);

        operationLogCmd.add(USER, userDb, DELETE_USER_DEPT,
            String.join(",", deptDb.stream().map(Dept::getName).toList()));
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> userAdd(Long deptId, List<DeptUser> deptUsers) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      Dept deptDb;
      Set<Long> userIds;
      List<User> users;

      @Override
      protected void checkParams() {
        // Check the department existed
        deptDb = deptQuery.checkAndFind(deptId);
        // Check the department user quota
        userDeptQuery.checkDeptUserAppendQuota(getOptTenantId(), deptUsers.size(), deptId);

        // Check the users existed
        userIds = deptUsers.stream().map(DeptUser::getUserId).collect(Collectors.toSet());
        users = userQuery.checkAndFind(userIds);
        // Check the user department quota
        for (Long userId : userIds) {
          userDeptQuery.checkUserDeptAppendQuota(getOptTenantId(), 1, userId);
        }
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        // @DoInFuture("Set user mainDeptId")

        deptUserRepo.deleteByDeptIdInAndUserIdIn(Collections.singleton(deptId), userIds);

        List<IdKey<Long, Object>> idKeys = batchInsert(deptUsers);

        operationLogCmd.add(DEPT, deptDb, ADD_DEPT_USER,
            users.stream().map(User::getName).collect(Collectors.joining(",")));
        return idKeys;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void userDelete(Long deptId, HashSet<Long> userIds) {
    new BizTemplate<Void>() {
      Dept deptDb;
      List<User> usersDb;

      @Override
      protected void checkParams() {
        deptDb = deptQuery.checkAndFind(deptId);
        usersDb = userQuery.checkAndFind(userIds);
      }

      @Override
      protected Void process() {
        deptUserRepo.deleteByDeptIdAndUserIdIn(deptId, userIds);
        userCmd.clearMainDeptByDeptIdAndUserIdIn(deptId, userIds);

        operationLogCmd.add(DEPT, deptDb, DELETE_DEPT_USER,
            usersDb.stream().map(User::getName).collect(Collectors.joining(",")));
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void headReplace(Long deptId, Long userId, Boolean head) {
    new BizTemplate<Void>() {
      Dept deptDb;
      User userDb;

      @Override
      protected void checkParams() {
        deptDb = deptQuery.checkAndFind(deptId);
        userDb = userQuery.checkAndFind(userId);
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

        operationLogCmd.add(USER, userDb, head ? SET_USER_MAIN_DEPT
            : CANCEL_USER_MAIN_DEPT, deptDb.getName());
        return null;
      }
    }.execute();
  }

  @Override
  public List<IdKey<Long, Object>> add0(List<DeptUser> deptUsers) {
    // The user must and can only have one primary department
    assertTrue(deptUsers.stream()
        .filter(DeptUser::getMainDept).count() == 1, USER_MAIN_DEPT_NUM_ERROR);
    // Check departments existed
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
