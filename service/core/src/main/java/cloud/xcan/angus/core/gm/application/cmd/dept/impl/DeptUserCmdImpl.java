package cloud.xcan.angus.core.gm.application.cmd.dept.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.gm.domain.UserMessage.USER_MAIN_DEPT_NUM_ERROR;
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

/**
 * Implementation of department user command operations for managing user-department relationships.
 * 
 * <p>This class provides comprehensive functionality for department-user management including:</p>
 * <ul>
 *   <li>Adding users to departments and departments to users</li>
 *   <li>Managing user main department assignments</li>
 *   <li>Handling department head assignments</li>
 *   <li>Managing department-user quotas and validations</li>
 *   <li>Recording operation logs for audit trails</li>
 * </ul>
 * 
 * <p>The implementation ensures proper user-department relationship management
 * with quota controls and audit trail maintenance.</p>
 */
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

  /**
   * Adds departments to a user with comprehensive validation.
   * 
   * <p>This method performs department assignment including:</p>
   * <ul>
   *   <li>Validating user existence</li>
   *   <li>Checking user department quota limits</li>
   *   <li>Validating department existence</li>
   *   <li>Checking department user quota limits</li>
   *   <li>Creating department-user associations</li>
   *   <li>Recording operation audit logs</li>
   * </ul>
   * 
   * @param userId User identifier
   * @param deptUsers List of department-user associations to create
   * @return List of created association identifiers
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> deptAdd(Long userId, List<DeptUser> deptUsers) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      User userDb;
      Set<Long> deptIds;
      List<Dept> deptDb;

      @Override
      protected void checkParams() {
        // Validate user exists
        userDb = userQuery.checkAndFind(userId);
        // Validate user department quota
        userDeptQuery.checkUserDeptAppendQuota(getOptTenantId(), deptUsers.size(), userId);

        // Validate departments exist
        deptIds = deptUsers.stream().map(DeptUser::getDeptId).collect(Collectors.toSet());
        deptDb = deptQuery.checkAndFind(deptIds);
        // Validate department user quota
        for (Long deptId : deptIds) {
          userDeptQuery.checkDeptUserAppendQuota(getOptTenantId(), 1, deptId);
        }
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        // Note: Future enhancement to set user mainDeptId

        // Remove existing associations and create new ones
        deptUserRepo.deleteByUserIdAndDeptIdIn(userId, deptIds);
        List<IdKey<Long, Object>> idKeys = batchInsert(deptUsers);

        // Record operation audit log
        operationLogCmd.add(USER, userDb, ADD_USER_DEPT,
            String.join(",", deptDb.stream().map(Dept::getName).toList()));
        return idKeys;
      }
    }.execute();
  }

  /**
   * Replaces user's department associations with new ones.
   * 
   * <p>This method performs comprehensive department replacement including:</p>
   * <ul>
   *   <li>Validating user and department existence</li>
   *   <li>Checking quota limits for replacement</li>
   *   <li>Setting user's main department</li>
   *   <li>Clearing existing associations</li>
   *   <li>Creating new associations</li>
   *   <li>Recording operation audit logs</li>
   * </ul>
   * 
   * @param userId User identifier
   * @param deptUsers List of new department-user associations
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void deptReplace(Long userId, List<DeptUser> deptUsers) {
    new BizTemplate<Void>() {
      User userDb;
      List<Dept> deptDb;

      @Override
      protected void checkParams() {
        // Validate user exists
        userDb = userQuery.checkAndFind(userId);
        // Validate departments exist
        Set<Long> deptIds = deptUsers.stream().map(DeptUser::getDeptId).collect(Collectors.toSet());
        deptDb = deptQuery.checkAndFind(deptIds);

        // Validate user department quota
        userDeptQuery.checkUserDeptReplaceQuota(getOptTenantId(), deptUsers.size(), userId);
        for (DeptUser userDept : deptUsers) {
          // Validate department user quota
          userDeptQuery.checkDeptUserAppendQuota(getOptTenantId(), 1, userDept.getDeptId());
        }
      }

      @Override
      protected Void process() {
        // Set user's main department
        DeptUser mainDept = deptUsers.stream().filter(DeptUser::getMainDept).findFirst()
            .orElse(deptUsers.get(0));
        userDb.setMainDeptId(mainDept.getDeptId());
        userRepo.save(userDb);

        // Clear existing associations
        deleteByUserId(Collections.singleton(userId));
        // Create new associations
        if (isNotEmpty(deptUsers)) {
          add0(deptUsers.stream().peek(deptUser -> deptUser.setTenantId(userDb.getTenantId()))
              .collect(Collectors.toList()));
        }

        // Record operation audit log
        operationLogCmd.add(USER, userDb, UPDATE_USER_DEPT,
            String.join(",", deptDb.stream().map(Dept::getName).toList()));
        return null;
      }
    }.execute();
  }

  /**
   * Removes departments from a user.
   * 
   * <p>This method performs department removal including:</p>
   * <ul>
   *   <li>Validating user and department existence</li>
   *   <li>Removing department-user associations</li>
   *   <li>Clearing user main department references</li>
   *   <li>Recording operation audit logs</li>
   * </ul>
   * 
   * @param userId User identifier
   * @param deptIds Set of department identifiers to remove
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void deptDelete(Long userId, HashSet<Long> deptIds) {
    new BizTemplate<Void>() {
      User userDb;
      List<Dept> deptDb;

      @Override
      protected void checkParams() {
        // Validate user exists
        userDb = userQuery.checkAndFind(userId);
        // Validate departments exist
        deptDb = deptQuery.checkAndFind(deptIds);
      }

      @Override
      protected Void process() {
        // Remove department-user associations
        deptUserRepo.deleteByUserIdAndDeptIdIn(userId, deptIds);
        // Clear user main department references
        userCmd.clearMainDeptByUserIdAndDeptIdIn(userId, deptIds);

        // Record operation audit log
        operationLogCmd.add(USER, userDb, DELETE_USER_DEPT,
            String.join(",", deptDb.stream().map(Dept::getName).toList()));
        return null;
      }
    }.execute();
  }

  /**
   * Adds users to a department with comprehensive validation.
   * 
   * <p>This method performs user assignment including:</p>
   * <ul>
   *   <li>Validating department existence</li>
   *   <li>Checking department user quota limits</li>
   *   <li>Validating user existence</li>
   *   <li>Checking user department quota limits</li>
   *   <li>Creating user-department associations</li>
   *   <li>Recording operation audit logs</li>
   * </ul>
   * 
   * @param deptId Department identifier
   * @param deptUsers List of user-department associations to create
   * @return List of created association identifiers
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> userAdd(Long deptId, List<DeptUser> deptUsers) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      Dept deptDb;
      Set<Long> userIds;
      List<User> users;

      @Override
      protected void checkParams() {
        // Validate department exists
        deptDb = deptQuery.checkAndFind(deptId);
        // Validate department user quota
        userDeptQuery.checkDeptUserAppendQuota(getOptTenantId(), deptUsers.size(), deptId);

        // Validate users exist
        userIds = deptUsers.stream().map(DeptUser::getUserId).collect(Collectors.toSet());
        users = userQuery.checkAndFind(userIds);
        // Validate user department quota
        for (Long userId : userIds) {
          userDeptQuery.checkUserDeptAppendQuota(getOptTenantId(), 1, userId);
        }
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        // Note: Future enhancement to set user mainDeptId

        // Remove existing associations and create new ones
        deptUserRepo.deleteByDeptIdInAndUserIdIn(Collections.singleton(deptId), userIds);

        List<IdKey<Long, Object>> idKeys = batchInsert(deptUsers);

        // Record operation audit log
        operationLogCmd.add(DEPT, deptDb, ADD_DEPT_USER,
            users.stream().map(User::getName).collect(Collectors.joining(",")));
        return idKeys;
      }
    }.execute();
  }

  /**
   * Removes users from a department.
   * 
   * <p>This method performs user removal including:</p>
   * <ul>
   *   <li>Validating department and user existence</li>
   *   <li>Removing user-department associations</li>
   *   <li>Clearing user main department references</li>
   *   <li>Recording operation audit logs</li>
   * </ul>
   * 
   * @param deptId Department identifier
   * @param userIds Set of user identifiers to remove
   */
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
        // Remove user-department associations
        deptUserRepo.deleteByDeptIdAndUserIdIn(deptId, userIds);
        // Clear user main department references
        userCmd.clearMainDeptByDeptIdAndUserIdIn(deptId, userIds);

        // Record operation audit log
        operationLogCmd.add(DEPT, deptDb, DELETE_DEPT_USER,
            usersDb.stream().map(User::getName).collect(Collectors.joining(",")));
        return null;
      }
    }.execute();
  }

  /**
   * Replaces department head assignment.
   * 
   * <p>This method manages department head assignments including:</p>
   * <ul>
   *   <li>Validating department and user existence</li>
   *   <li>Managing department head status</li>
   *   <li>Updating user department head status</li>
   *   <li>Recording operation audit logs</li>
   * </ul>
   * 
   * @param deptId Department identifier
   * @param userId User identifier
   * @param head Whether user should be department head
   */
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
        // Cancel other department heads if setting new head
        if (head) {
          deptUserRepo.updateDeptHead(deptId, head);
        }

        // Update user department head status
        deptUserRepo.updateDeptHead(deptId, userId, head);
        userRepo.updateDeptHead(userId, head);

        // Record operation audit log
        operationLogCmd.add(USER, userDb, head ? SET_USER_MAIN_DEPT
            : CANCEL_USER_MAIN_DEPT, deptDb.getName());
        return null;
      }
    }.execute();
  }

  /**
   * Adds department-user associations with validation.
   * 
   * <p>This method ensures users can only have one primary department
   * and validates department existence.</p>
   * 
   * @param deptUsers List of department-user associations to create
   * @return List of created association identifiers
   */
  @Override
  public List<IdKey<Long, Object>> add0(List<DeptUser> deptUsers) {
    // Validate user can only have one primary department
    assertTrue(deptUsers.stream()
        .filter(DeptUser::getMainDept).count() == 1, USER_MAIN_DEPT_NUM_ERROR);
    // Validate departments exist
    deptQuery.checkAndFind(deptUsers.stream().map(DeptUser::getDeptId)
        .collect(Collectors.toList()));
    return batchInsert(new HashSet<>(deptUsers));
  }

  /**
   * Deletes department-user associations by user identifiers.
   * 
   * @param userIds Set of user identifiers
   */
  @Override
  public void deleteByUserId(Set<Long> userIds) {
    deptUserRepo.deleteAllByUserIdIn(userIds);
  }

  /**
   * Deletes department-user associations by tenant identifiers.
   * 
   * @param tenantIds Set of tenant identifiers
   */
  @Override
  public void deleteByTenantId(Set<Long> tenantIds) {
    deptUserRepo.deleteAllByTenantIdIn(tenantIds);
  }

  /**
   * Deletes department-user associations by department identifiers.
   * 
   * @param deptIds Collection of department identifiers
   */
  @Override
  public void deleteAllByDeptId(Collection<Long> deptIds) {
    deptUserRepo.deleteAllByDeptIdIn(deptIds);
  }

  @Override
  protected BaseRepository<DeptUser, Long> getRepository() {
    return this.deptUserRepo;
  }
}
