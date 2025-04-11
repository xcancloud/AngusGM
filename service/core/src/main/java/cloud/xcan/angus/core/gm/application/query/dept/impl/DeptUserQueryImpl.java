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
import cloud.xcan.angus.core.biz.Biz;
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

@Biz
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

  private void setAssociationDept(Page<DeptUser> userDeptPage) {
    Map<Long, Dept> groupMap = deptRepo.findAllById(userDeptPage.getContent().stream()
            .map(DeptUser::getDeptId).collect(Collectors.toList()))
        .stream().collect(Collectors.toMap(Dept::getId, x -> x));
    for (DeptUser deptUser : userDeptPage.getContent()) {
      deptUser.setDept(groupMap.get(deptUser.getDeptId()));
    }
  }

  private void setAssociationUser(Page<DeptUser> userDeptPage) {
    Map<Long, User> userMap = userRepo.findAllById(userDeptPage.getContent().stream()
            .map(DeptUser::getUserId).collect(Collectors.toList()))
        .stream().collect(Collectors.toMap(User::getId, x -> x));
    for (DeptUser deptUser : userDeptPage.getContent()) {
      deptUser.setUser(userMap.get(deptUser.getUserId()));
    }
  }

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

  @Override
  public void checkDeptUserAppendQuota(Long tenantId, long incr, Long deptId) {
    if (incr > 0) {
      long num = deptUserRepo.countByTenantIdAndDeptId(tenantId, deptId);
      settingTenantQuotaManager
          .checkTenantQuota(QuotaResource.DeptUser, Collections.singleton(deptId), num + incr);
    }
  }

  @Override
  public void checkUserDeptReplaceQuota(Long tenantId, long incr, Long userId) {
    if (incr > 0) {
      // long num = deptUserRepo.countByTenantIdAndUserId(tenantId, userId); <- Replace user depts
      settingTenantQuotaManager
          .checkTenantQuota(QuotaResource.UserDept, Collections.singleton(userId), /*num +*/ incr);
    }
  }

  @Override
  public void checkUserDeptAppendQuota(Long tenantId, long incr, Long userId) {
    if (incr > 0) {
      long num = deptUserRepo.countByTenantIdAndUserId(tenantId, userId);
      settingTenantQuotaManager
          .checkTenantQuota(QuotaResource.UserDept, Collections.singleton(userId), num + incr);
    }
  }
}
