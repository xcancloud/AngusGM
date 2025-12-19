package cloud.xcan.angus.core.gm.application.query.dept.impl;

import static cloud.xcan.angus.core.jpa.criteria.CriteriaUtils.findFirstValue;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.dept.Dept;
import cloud.xcan.angus.api.commonlink.dept.DeptRepo;
import cloud.xcan.angus.api.commonlink.setting.quota.QuotaResource;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.commonlink.user.UserRepo;
import cloud.xcan.angus.api.commonlink.user.dept.DeptUser;
import cloud.xcan.angus.api.commonlink.user.dept.DeptUserRepo;
import cloud.xcan.angus.api.manager.SettingTenantQuotaManager;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.ProtocolAssert;
import cloud.xcan.angus.core.gm.application.converter.UserConverter;
import cloud.xcan.angus.core.gm.application.query.dept.DeptUserQuery;
import cloud.xcan.angus.core.gm.domain.dept.user.DeptUserListRepo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import jakarta.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * <p>
 * Implementation of department user query operations.
 * </p>
 * <p>
 * Manages department-user relationship queries, validation, and quota management. Provides
 * comprehensive department-user querying with association support.
 * </p>
 * <p>
 * Supports user-department queries, department-user queries, association management, and quota
 * validation for comprehensive department-user administration.
 * </p>
 */
@org.springframework.stereotype.Service
public class DeptUserQueryImpl implements DeptUserQuery {

  @Resource
  private DeptRepo deptRepo;
  @Resource
  private UserRepo userRepo;
  @Resource
  private DeptUserRepo deptUserRepo;
  @Resource
  private DeptUserListRepo deptUserListRepo;
  @Resource
  private SettingTenantQuotaManager settingTenantQuotaManager;

  /**
   * <p>
   * Retrieves user-department relationships with pagination.
   * </p>
   * <p>
   * Queries department associations for specific users with validation. Requires userId parameter
   * for proper filtering.
   * </p>
   */
  @Override
  public Page<DeptUser> findUserDept(GenericSpecification<DeptUser> spec,
      Pageable pageable) {
    return new BizTemplate<Page<DeptUser>>(true, true) {
      @Override
      protected void checkParams() {
        String userId = findFirstValue(spec.getCriteria(), "userId");
        ProtocolAssert.assertNotEmpty(userId, "userId is required");
      }

      @Override
      protected Page<DeptUser> process() {
        Page<DeptUser> userDeptPage = deptUserListRepo.find(spec.getCriteria(),
            pageable, DeptUser.class, UserConverter::objectArrToDeptUser, null);
        setHasSubDept(userDeptPage.getContent());
        return userDeptPage;
      }
    }.execute();
  }

  /**
   * <p>
   * Retrieves department-user relationships with pagination.
   * </p>
   * <p>
   * Queries user associations for specific departments with validation. Requires deptId parameter
   * for proper filtering.
   * </p>
   */
  @Override
  public Page<DeptUser> findDeptUser(GenericSpecification<DeptUser> spec,
      Pageable pageable) {
    return new BizTemplate<Page<DeptUser>>(true, true) {
      @Override
      protected void checkParams() {
        String deptId = findFirstValue(spec.getCriteria(), "deptId");
        ProtocolAssert.assertNotEmpty(deptId, "deptId is required");
      }

      @Override
      protected Page<DeptUser> process() {
        Page<DeptUser> deptUserPage = deptUserListRepo.find(spec.getCriteria(),
            pageable, DeptUser.class, UserConverter::objectArrToDeptUser, null);
        setHasSubDept(deptUserPage.getContent());
        return deptUserPage;
      }
    }.execute();
  }

  /**
   * <p>
   * Sets sub-department status for department-user list.
   * </p>
   * <p>
   * Identifies departments that have sub-departments and sets hasSubDept flag.
   * </p>
   */
  @Override
  public void setHasSubDept(List<DeptUser> deptUsersDb) {
    if (isEmpty(deptUsersDb)) {
      return;
    }
    Set<Long> hasSubDeptIds = deptRepo
        .findPidBySubPid(deptUsersDb.stream().map(DeptUser::getId).collect(Collectors.toSet()));
    for (DeptUser dept : deptUsersDb) {
      dept.setHasSubDept(hasSubDeptIds.contains(dept.getId()));
    }
  }

  /**
   * <p>
   * Sets department associations for department-user page.
   * </p>
   * <p>
   * Loads department information and associates with department-user records.
   * </p>
   */
  private void setAssociationDept(Page<DeptUser> userDeptPage) {
    Map<Long, Dept> groupMap = deptRepo.findAllById(userDeptPage.getContent().stream()
            .map(DeptUser::getDeptId).collect(Collectors.toList()))
        .stream().collect(Collectors.toMap(Dept::getId, x -> x));
    for (DeptUser deptUser : userDeptPage.getContent()) {
      deptUser.setDept(groupMap.get(deptUser.getDeptId()));
    }
  }

  /**
   * <p>
   * Sets user associations for department-user page.
   * </p>
   * <p>
   * Loads user information and associates with department-user records.
   * </p>
   */
  private void setAssociationUser(Page<DeptUser> userDeptPage) {
    Map<Long, User> userMap = userRepo.findAllById(userDeptPage.getContent().stream()
            .map(DeptUser::getUserId).collect(Collectors.toList()))
        .stream().collect(Collectors.toMap(User::getId, x -> x));
    for (DeptUser deptUser : userDeptPage.getContent()) {
      deptUser.setUser(userMap.get(deptUser.getUserId()));
    }
  }

  /**
   * <p>
   * Retrieves all department associations for specific user.
   * </p>
   * <p>
   * Returns all departments associated with the specified user. Loads department information for
   * complete association data.
   * </p>
   */
  @Override
  public List<DeptUser> findAllByUserId(Long userId) {
    List<DeptUser> deptUsers = deptUserRepo.findAllByUserId(userId);
    if (isNotEmpty(deptUsers)) {
      List<Dept> depts = deptRepo.findAllById(deptUsers.stream().map(DeptUser::getDeptId)
          .collect(Collectors.toList()));
      if (isNotEmpty(depts)) {
        Map<Long, Dept> deptMap = depts.stream().collect(Collectors.toMap(Dept::getId, x -> x));
        for (DeptUser deptUser : deptUsers) {
          deptUser.setDept(deptMap.get(deptUser.getDeptId()));
        }
      }
    }
    return deptUsers;
  }

  /**
   * <p>
   * Validates department-user append quota for tenant.
   * </p>
   * <p>
   * Checks if adding users to department would exceed tenant quota limits. Throws appropriate
   * exception if quota would be exceeded.
   * </p>
   */
  @Override
  public void checkDeptUserAppendQuota(Long tenantId, long incr, Long deptId) {
    if (incr > 0) {
      long num = deptUserRepo.countByTenantIdAndDeptId(tenantId, deptId);
      settingTenantQuotaManager
          .checkTenantQuota(QuotaResource.DeptUser, Collections.singleton(deptId), num + incr);
    }
  }

  /**
   * <p>
   * Validates user-department replace quota for tenant.
   * </p>
   * <p>
   * Checks if replacing user departments would exceed tenant quota limits. Throws appropriate
   * exception if quota would be exceeded.
   * </p>
   */
  @Override
  public void checkUserDeptReplaceQuota(Long tenantId, long incr, Long userId) {
    if (incr > 0) {
      // long num = deptUserRepo.countByTenantIdAndUserId(tenantId, userId); <- Replace user depts
      settingTenantQuotaManager
          .checkTenantQuota(QuotaResource.UserDept, Collections.singleton(userId), /*num +*/ incr);
    }
  }

  /**
   * <p>
   * Validates user-department append quota for tenant.
   * </p>
   * <p>
   * Checks if adding departments to user would exceed tenant quota limits. Throws appropriate
   * exception if quota would be exceeded.
   * </p>
   */
  @Override
  public void checkUserDeptAppendQuota(Long tenantId, long incr, Long userId) {
    if (incr > 0) {
      long num = deptUserRepo.countByTenantIdAndUserId(tenantId, userId);
      settingTenantQuotaManager
          .checkTenantQuota(QuotaResource.UserDept, Collections.singleton(userId), num + incr);
    }
  }
}
