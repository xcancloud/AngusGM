package cloud.xcan.angus.core.gm.application.cmd.app.impl;

import static cloud.xcan.angus.core.gm.application.converter.AuthPolicyOrgConverter.addToPolicyDept;
import static cloud.xcan.angus.core.gm.application.converter.AuthPolicyOrgConverter.addToPolicyGroup;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.AuthOrgType;
import cloud.xcan.angus.api.commonlink.tag.OrgTargetType;
import cloud.xcan.angus.api.manager.UserManager;
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

/**
 * Implementation of application organization authorization command operations.
 *
 * <p>This class provides comprehensive functionality for managing organization-based
 * authorization policies including:</p>
 * <ul>
 *   <li>Assigning authorization policies to users, departments, and groups</li>
 *   <li>Removing authorization policies from organizations</li>
 *   <li>Validating organization and policy existence</li>
 *   <li>Handling policy authorization permissions</li>
 * </ul>
 *
 * <p>The implementation ensures proper authorization management across different
 * organization types (users, departments, groups) within applications.</p>
 */
@org.springframework.stereotype.Service
public class AppOrgAuthCmdImpl extends CommCmd<AuthPolicyOrg, Long> implements AppOrgAuthCmd {

  @Resource
  private AuthPolicyOrgRepo authPolicyOrgRepo;
  @Resource
  private AuthPolicyQuery authPolicyQuery;
  @Resource
  private UserManager userManager;

  /**
   * Assigns authorization policies to users.
   *
   * <p>This method creates authorization associations between users and policies,
   * ignoring existing associations to avoid duplicates.</p>
   *
   * @param appId     Application identifier
   * @param userIds   Set of user identifiers
   * @param policyIds Set of policy identifiers
   * @return List of created authorization identifiers
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> authUserPolicy(Long appId, HashSet<Long> userIds,
      HashSet<Long> policyIds) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      @Override
      protected void checkParams() {
        // Validate that users exist
        userManager.checkOrgExists(OrgTargetType.USER, userIds);

        // Validate policy authorization permissions
        authPolicyQuery.checkAuthPolicyPermission(appId, policyIds);
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        // Ignore existing policy authorizations to avoid duplicates
        List<AuthPolicy> authPoliciesDb = authPolicyQuery
            .checkAndFindTenantPolicy(policyIds, true, true);
        List<AuthPolicyOrg> authPolicyOrg = AuthPolicyOrgConverter
            .addToPolicyUser(appId, authPoliciesDb, userIds);
        List<AuthPolicyOrg> authPolicyOrgDb = authPolicyOrgRepo
            .findByAppIdAndOrgIdInAndOrgTypeAndPolicyIdIn(appId, userIds, AuthOrgType.USER,
                policyIds);
        CoreUtils.removeAll(authPolicyOrg, authPolicyOrgDb);

        // Save new policy authorizations
        return isEmpty(authPolicyOrg) ? null : batchInsert(authPolicyOrg);
      }
    }.execute();
  }

  /**
   * Assigns authorization policies to departments.
   *
   * <p>This method creates authorization associations between departments and policies,
   * ignoring existing associations to avoid duplicates.</p>
   *
   * @param appId     Application identifier
   * @param deptIds   Set of department identifiers
   * @param policyIds Set of policy identifiers
   * @return List of created authorization identifiers
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> authDeptPolicy(Long appId, HashSet<Long> deptIds,
      HashSet<Long> policyIds) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      @Override
      protected void checkParams() {
        // Validate that departments exist
        userManager.checkOrgExists(OrgTargetType.DEPT, deptIds);

        // Validate policy authorization permissions
        authPolicyQuery.checkAuthPolicyPermission(appId, policyIds);
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        // Ignore existing policy authorizations to avoid duplicates
        List<AuthPolicy> authPoliciesDb = authPolicyQuery
            .checkAndFindTenantPolicy(policyIds, true, true);
        List<AuthPolicyOrg> authPolicyOrg = addToPolicyDept(appId, authPoliciesDb, deptIds);
        List<AuthPolicyOrg> authPolicyOrgDb
            = authPolicyOrgRepo.findByAppIdAndOrgIdInAndOrgTypeAndPolicyIdIn(
            appId, deptIds, AuthOrgType.DEPT, policyIds);
        CoreUtils.removeAll(authPolicyOrg, authPolicyOrgDb);

        // Save new policy authorizations
        return isEmpty(authPolicyOrg) ? null : batchInsert(authPolicyOrg);
      }
    }.execute();
  }

  /**
   * Assigns authorization policies to groups.
   *
   * <p>This method creates authorization associations between groups and policies,
   * ignoring existing associations to avoid duplicates.</p>
   *
   * @param appId     Application identifier
   * @param groupIds  Set of group identifiers
   * @param policyIds Set of policy identifiers
   * @return List of created authorization identifiers
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> authGroupPolicy(Long appId, HashSet<Long> groupIds,
      HashSet<Long> policyIds) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      @Override
      protected void checkParams() {
        // Validate that groups exist
        userManager.checkOrgExists(OrgTargetType.GROUP, groupIds);

        // Validate policy authorization permissions
        authPolicyQuery.checkAuthPolicyPermission(appId, policyIds);
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        // Ignore existing policy authorizations to avoid duplicates
        List<AuthPolicy> authPoliciesDb = authPolicyQuery
            .checkAndFindTenantPolicy(policyIds, true, true);
        List<AuthPolicyOrg> authPolicyOrg = addToPolicyGroup(appId, authPoliciesDb, groupIds);
        List<AuthPolicyOrg> authPolicyOrgDb =
            authPolicyOrgRepo.findByAppIdAndOrgIdInAndOrgTypeAndPolicyIdIn(
                appId, groupIds, AuthOrgType.GROUP, policyIds);
        CoreUtils.removeAll(authPolicyOrg, authPolicyOrgDb);

        // Save new policy authorizations
        return isEmpty(authPolicyOrg) ? null : batchInsert(authPolicyOrg);
      }
    }.execute();
  }

  /**
   * Removes authorization policies from users.
   *
   * <p>This method removes authorization associations between users and policies.
   * When policyIds is empty, all user authorizations are deleted, excluding those of other
   * associated organizations. To cancel all authorization completely, the global default
   * authorization must also be canceled.</p>
   *
   * @param appId     Application identifier
   * @param userIds   Set of user identifiers
   * @param policyIds Set of policy identifiers (optional)
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void authUserPolicyDelete(Long appId, HashSet<Long> userIds, HashSet<Long> policyIds) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        // Validate that users exist
        userManager.checkOrgExists(OrgTargetType.DEPT, userIds);

        // Validate policy authorization permissions
        authPolicyQuery.checkAuthPolicyPermission(appId, policyIds);
      }

      @Override
      protected Void process() {
        if (isNotEmpty(policyIds)) {
          // Remove specific policy authorizations for users
          authPolicyOrgRepo.deleteByAppIdAndOrgTypeAndOrgIdInAndPolicyIdIn(appId,
              OrgTargetType.GROUP.getValue(), userIds, policyIds);
        } else {
          // Remove all policy authorizations for users
          authPolicyOrgRepo.deleteByAppIdAndOrgTypeAndOrgIdIn(appId,
              OrgTargetType.GROUP.getValue(), userIds);
        }
        return null;
      }
    }.execute();
  }

  /**
   * Removes authorization policies from departments.
   *
   * <p>This method removes authorization associations between departments and policies.
   * When policyIds is empty, all department authorizations are deleted. To cancel all authorization
   * completely, the global default authorization must also be canceled.</p>
   *
   * @param appId     Application identifier
   * @param deptIds   Set of department identifiers
   * @param policyIds Set of policy identifiers (optional)
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void authDeptPolicyDelete(Long appId, HashSet<Long> deptIds, HashSet<Long> policyIds) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        // Validate that departments exist
        userManager.checkOrgExists(OrgTargetType.DEPT, deptIds);

        // Validate policy authorization permissions
        authPolicyQuery.checkAuthPolicyPermission(appId, policyIds);
      }

      @Override
      protected Void process() {
        if (isNotEmpty(policyIds)) {
          // Remove specific policy authorizations for departments
          authPolicyOrgRepo.deleteByAppIdAndOrgTypeAndOrgIdInAndPolicyIdIn(appId,
              OrgTargetType.DEPT.getValue(), deptIds, policyIds);
        } else {
          // Remove all policy authorizations for departments
          authPolicyOrgRepo.deleteByAppIdAndOrgTypeAndOrgIdIn(appId,
              OrgTargetType.DEPT.getValue(), deptIds);
        }
        return null;
      }
    }.execute();
  }

  /**
   * Removes authorization policies from groups.
   *
   * <p>This method removes authorization associations between groups and policies.
   * When policyIds is empty, all group authorizations are deleted. To cancel all authorization
   * completely, the global default authorization must also be canceled.</p>
   *
   * @param appId     Application identifier
   * @param groupIds  Set of group identifiers
   * @param policyIds Set of policy identifiers (optional)
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void authGroupPolicyDelete(Long appId, HashSet<Long> groupIds, HashSet<Long> policyIds) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        // Validate that groups exist
        userManager.checkOrgExists(OrgTargetType.GROUP, groupIds);

        // Validate policy authorization permissions
        authPolicyQuery.checkAuthPolicyPermission(appId, policyIds);
      }

      @Override
      protected Void process() {
        if (isNotEmpty(policyIds)) {
          // Remove specific policy authorizations for groups
          authPolicyOrgRepo.deleteByAppIdAndOrgTypeAndOrgIdInAndPolicyIdIn(appId,
              OrgTargetType.GROUP.getValue(), groupIds, policyIds);
        } else {
          // Remove all policy authorizations for groups
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
