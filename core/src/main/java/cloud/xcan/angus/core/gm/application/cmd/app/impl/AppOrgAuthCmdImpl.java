package cloud.xcan.angus.core.gm.application.cmd.app.impl;

import static cloud.xcan.angus.core.gm.application.converter.AuthPolicyOrgConverter.addToPolicyDept;
import static cloud.xcan.angus.core.gm.application.converter.AuthPolicyOrgConverter.addToPolicyGroup;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.AuthOrgType;
import cloud.xcan.angus.api.commonlink.tag.OrgTargetType;
import cloud.xcan.angus.api.manager.UserManager;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.app.AppOrgAuthCmd;
import cloud.xcan.angus.core.gm.application.converter.AuthPolicyOrgConverter;
import cloud.xcan.angus.core.gm.application.query.policy.AuthPolicyQuery;
import cloud.xcan.angus.core.gm.domain.policy.AuthPolicy;
import cloud.xcan.angus.core.gm.domain.policy.org.AuthPolicyOrg;
import cloud.xcan.angus.core.gm.domain.policy.org.AuthPolicyOrgRepo;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.utils.CoreUtils;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Biz
public class AppOrgAuthCmdImpl extends CommCmd<AuthPolicyOrg, Long> implements AppOrgAuthCmd {

  @Resource
  private AuthPolicyOrgRepo authPolicyOrgRepo;

  @Resource
  private AuthPolicyQuery authPolicyQuery;

  @Resource
  private UserManager userManager;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> authUserPolicy(Long appId, HashSet<Long> userIds,
      HashSet<Long> policyIds) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      @Override
      protected void checkParams() {
        // Check the users existed
        userManager.checkOrgExists(OrgTargetType.USER, userIds);

        // Check the policy authorization permissions
        authPolicyQuery.checkAuthPolicyPermission(appId, policyIds);
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        // Ignore existed policies authorization
        List<AuthPolicy> authPoliciesDb = authPolicyQuery
            .checkAndFindTenantPolicy(policyIds, true, true);
        List<AuthPolicyOrg> authPolicyOrg = AuthPolicyOrgConverter
            .addToPolicyUser(appId, authPoliciesDb, userIds);
        List<AuthPolicyOrg> authPolicyOrgDb = authPolicyOrgRepo
            .findByAppIdAndOrgIdInAndOrgTypeAndPolicyIdIn(appId, userIds, AuthOrgType.USER,
                policyIds);
        CoreUtils.removeAll(authPolicyOrg, authPolicyOrgDb);

        // Save new policies authorization
        return isEmpty(authPolicyOrg) ? null : batchInsert(authPolicyOrg);
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> authDeptPolicy(Long appId, HashSet<Long> deptIds,
      HashSet<Long> policyIds) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      @Override
      protected void checkParams() {
        // Check the users existed
        userManager.checkOrgExists(OrgTargetType.DEPT, deptIds);

        // Check the policy authorization permissions
        authPolicyQuery.checkAuthPolicyPermission(appId, policyIds);
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        // Ignore existed policies authorization
        List<AuthPolicy> authPoliciesDb = authPolicyQuery
            .checkAndFindTenantPolicy(policyIds, true, true);
        List<AuthPolicyOrg> authPolicyOrg = addToPolicyDept(appId, authPoliciesDb, deptIds);
        List<AuthPolicyOrg> authPolicyOrgDb
            = authPolicyOrgRepo.findByAppIdAndOrgIdInAndOrgTypeAndPolicyIdIn(
            appId, deptIds, AuthOrgType.DEPT, policyIds);
        CoreUtils.removeAll(authPolicyOrg, authPolicyOrgDb);

        // Save new policies authorization
        return isEmpty(authPolicyOrg) ? null : batchInsert(authPolicyOrg);
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> authGroupPolicy(Long appId, HashSet<Long> groupIds,
      HashSet<Long> policyIds) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      @Override
      protected void checkParams() {
        // Check the users existed
        userManager.checkOrgExists(OrgTargetType.GROUP, groupIds);

        // Check the policy authorization permissions
        authPolicyQuery.checkAuthPolicyPermission(appId, policyIds);
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        // Ignore existed policies authorization
        List<AuthPolicy> authPoliciesDb = authPolicyQuery
            .checkAndFindTenantPolicy(policyIds, true, true);
        List<AuthPolicyOrg> authPolicyOrg = addToPolicyGroup(appId, authPoliciesDb, groupIds);
        List<AuthPolicyOrg> authPolicyOrgDb =
            authPolicyOrgRepo.findByAppIdAndOrgIdInAndOrgTypeAndPolicyIdIn(
                appId, groupIds, AuthOrgType.GROUP, policyIds);
        CoreUtils.removeAll(authPolicyOrg, authPolicyOrgDb);

        // Save new policies authorization
        return isEmpty(authPolicyOrg) ? null : batchInsert(authPolicyOrg);
      }
    }.execute();
  }

  /**
   * @param policyIds When it is empty, only users all authorizations will be deleted, excluding
   *                  those of other associated organizations authorizations. If you want to cancel
   *                  all authorization completely, you also need to cancel the global default
   *                  authorization
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void authUserPolicyDelete(Long appId, HashSet<Long> userIds, HashSet<Long> policyIds) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        // Check the users existed
        userManager.checkOrgExists(OrgTargetType.DEPT, userIds);

        // Check the policy authorization permissions
        authPolicyQuery.checkAuthPolicyPermission(appId, policyIds);
      }

      @Override
      protected Void process() {
        if (isNotEmpty(policyIds)) {
          authPolicyOrgRepo.deleteByAppIdAndOrgTypeAndOrgIdInAndPolicyIdIn(appId,
              OrgTargetType.GROUP.getValue(), userIds, policyIds);
        } else {
          authPolicyOrgRepo.deleteByAppIdAndOrgTypeAndOrgIdIn(appId,
              OrgTargetType.GROUP.getValue(), userIds);
        }
        return null;
      }
    }.execute();
  }

  /**
   * @param policyIds When it is empty, only departments all authorizations will be deleted. If you
   *                  want to cancel all authorization completely, you also need to cancel the
   *                  global default authorization
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void authDeptPolicyDelete(Long appId, HashSet<Long> deptIds, HashSet<Long> policyIds) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        // Check the users existed
        userManager.checkOrgExists(OrgTargetType.DEPT, deptIds);

        // Check the policy authorization permissions
        authPolicyQuery.checkAuthPolicyPermission(appId, policyIds);
      }

      @Override
      protected Void process() {
        if (isNotEmpty(policyIds)) {
          authPolicyOrgRepo.deleteByAppIdAndOrgTypeAndOrgIdInAndPolicyIdIn(appId,
              OrgTargetType.DEPT.getValue(), deptIds, policyIds);
        } else {
          authPolicyOrgRepo.deleteByAppIdAndOrgTypeAndOrgIdIn(appId,
              OrgTargetType.DEPT.getValue(), deptIds);
        }
        return null;
      }
    }.execute();
  }

  /**
   * @param policyIds When it is empty, only groups all authorizations will be deleted. If you want
   *                  to cancel all authorization completely, you also need to cancel the global
   *                  default authorization
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void authGroupPolicyDelete(Long appId, HashSet<Long> groupIds, HashSet<Long> policyIds) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        // Check the users existed
        userManager.checkOrgExists(OrgTargetType.GROUP, groupIds);

        // Check the policy authorization permissions
        authPolicyQuery.checkAuthPolicyPermission(appId, policyIds);
      }

      @Override
      protected Void process() {
        if (isNotEmpty(policyIds)) {
          authPolicyOrgRepo.deleteByAppIdAndOrgTypeAndOrgIdInAndPolicyIdIn(appId,
              OrgTargetType.GROUP.getValue(), groupIds, policyIds);
        } else {
          authPolicyOrgRepo.deleteByAppIdAndOrgTypeAndOrgIdIn(appId,
              OrgTargetType.GROUP.getValue(), groupIds);
        }
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<AuthPolicyOrg, Long> getRepository() {
    return authPolicyOrgRepo;
  }
}
