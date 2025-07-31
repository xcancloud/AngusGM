package cloud.xcan.angus.core.gm.application.cmd.policy.impl;

import static cloud.xcan.angus.core.gm.domain.operation.OperationResourceType.POLICY;
import static cloud.xcan.angus.core.gm.domain.operation.OperationResourceType.USER;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.ADD_POLICY_USER;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.ADD_USER_POLICY;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.DELETE_POLICY_USER;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.DELETE_USER_POLICY;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static java.util.Collections.singleton;

import cloud.xcan.angus.api.commonlink.AuthOrgType;
import cloud.xcan.angus.api.commonlink.tag.OrgTargetType;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.operation.OperationLogCmd;
import cloud.xcan.angus.core.gm.application.cmd.policy.AuthPolicyUserCmd;
import cloud.xcan.angus.core.gm.application.query.policy.AuthPolicyQuery;
import cloud.xcan.angus.core.gm.application.query.user.UserQuery;
import cloud.xcan.angus.core.gm.domain.policy.AuthPolicy;
import cloud.xcan.angus.core.gm.domain.policy.org.AuthPolicyOrg;
import cloud.xcan.angus.core.gm.domain.policy.org.AuthPolicyOrgRepo;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * Implementation of user authorization policy command operations.
 * </p>
 * <p>
 * Manages the association between users and authorization policies,
 * providing bidirectional operations for adding and removing policy-user relationships.
 * </p>
 * <p>
 * Supports both policy-centric and user-centric operations with proper
 * validation and audit logging.
 * </p>
 */
@Biz
@Slf4j
public class AuthPolicyUserCmdImpl extends CommCmd<AuthPolicyOrg, Long> implements
    AuthPolicyUserCmd {

  @Resource
  private AuthPolicyOrgRepo authPolicyOrgRepo;
  @Resource
  private AuthPolicyQuery authPolicyQuery;
  @Resource
  private UserQuery userQuery;
  @Resource
  private OperationLogCmd operationLogCmd;

  /**
   * <p>
   * Associates users with a specific authorization policy.
   * </p>
   * <p>
   * Validates that the policy and users exist, checks permissions,
   * and prevents duplicate associations.
   * </p>
   * <p>
   * Only creates new associations for users that aren't already
   * associated with the policy.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> policyUserAdd(Long policyId, List<AuthPolicyOrg> policyUsers) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      AuthPolicy policyDb;
      Set<Long> userIds;
      List<User> usersDb;

      @Override
      protected void checkParams() {
        // Verify policy exists
        policyDb = authPolicyQuery.checkAndFindTenantPolicy(policyId, false, false);
        // Verify users exist
        userIds = policyUsers.stream().map(AuthPolicyOrg::getOrgId).collect(Collectors.toSet());
        usersDb = userQuery.checkAndFind(userIds);
        // Validate policy permissions
        authPolicyQuery.checkAuthPolicyPermission(policyDb.getAppId(), singleton(policyId));
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        // Remove already authorized users to prevent duplicates
        List<Long> existedUserIds = authPolicyOrgRepo.findOrgIdsByPolicyIdAndOrgTypeAndOrgIdIn(
            policyId, OrgTargetType.USER.getValue(), userIds);
        userIds.removeAll(existedUserIds);

        if (isNotEmpty(userIds)) {
          // Complete authorization information
          assembleOrgAuthInfo(policyUsers, policyDb);

          // Save new user authorizations
          List<AuthPolicyOrg> addPolicyUsers = policyUsers.stream()
              .filter(x -> userIds.contains(x.getOrgId())).collect(Collectors.toList());
          if (isNotEmpty(addPolicyUsers)) {
            List<IdKey<Long, Object>> idKeys = batchInsert(addPolicyUsers);

            operationLogCmd.add(POLICY, policyDb, ADD_POLICY_USER,
                usersDb.stream().map(User::getName).collect(Collectors.joining(",")));
            return idKeys;
          }
        }
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Removes user associations from a specific authorization policy.
   * </p>
   * <p>
   * Validates that the policy and users exist, checks permissions,
   * and removes the specified user-policy associations.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void policyUserDelete(Long policyId, Set<Long> userIds) {
    new BizTemplate<Void>() {
      AuthPolicy policyDb;
      List<User> usersDb;

      @Override
      protected void checkParams() {
        // Verify policy exists
        policyDb = authPolicyQuery.checkAndFindTenantPolicy(policyId, false, false);
        // Verify users exist
        usersDb = userQuery.checkAndFind(userIds);
        // Validate policy permissions
        authPolicyQuery.checkAuthPolicyPermission(policyDb.getAppId(), singleton(policyId));
      }

      @Override
      protected Void process() {
        authPolicyOrgRepo.deleteByPolicyIdAndOrgTypeAndOrgIdIn(policyId,
            AuthOrgType.USER.getValue(), userIds);

        operationLogCmd.add(POLICY, policyDb, DELETE_POLICY_USER,
            usersDb.stream().map(User::getName).collect(Collectors.joining(",")));
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Associates authorization policies with a specific user.
   * </p>
   * <p>
   * Validates that the user and policies exist, checks permissions,
   * and prevents duplicate associations.
   * </p>
   * <p>
   * Only creates new associations for policies that aren't already
   * associated with the user.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> userPolicyAdd(Long userId, List<AuthPolicyOrg> userPolicies) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      User userDb;
      Set<Long> policyIds;
      List<AuthPolicy> policiesDb;

      @Override
      protected void checkParams() {
        // Verify user exists
        userDb = userQuery.checkAndFind(userId);
        // Verify policies exist
        policyIds = userPolicies.stream().map(AuthPolicyOrg::getPolicyId)
            .collect(Collectors.toSet());
        policiesDb = authPolicyQuery.checkAndFindTenantPolicy(policyIds, true, true);
        // Validate policy permissions
        authPolicyQuery.checkAuthPolicyPermission(policiesDb);
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        // Remove already authorized policies to prevent duplicates
        List<Long> existedPolicyIds = authPolicyOrgRepo.findPolicyIdsByUserIdAndOrgTypeAndPolicyIdIn(
            userId, OrgTargetType.USER.getValue(), policyIds);
        policyIds.removeAll(existedPolicyIds);

        if (isNotEmpty(policyIds)) {
          // Complete authorization information
          assemblePolicyAuthInfo(userPolicies, policiesDb);

          // Save new policy authorizations
          List<AuthPolicyOrg> newUserPolicies = userPolicies.stream()
              .filter(x -> policyIds.contains(x.getPolicyId())).collect(Collectors.toList());
          if (isNotEmpty(newUserPolicies)) {
            List<IdKey<Long, Object>> idKeys = batchInsert(newUserPolicies);

            operationLogCmd.add(USER, userDb, ADD_USER_POLICY,
                policiesDb.stream().map(AuthPolicy::getName).collect(Collectors.joining(",")));
            return idKeys;
          }
        }
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Removes authorization policy associations from a specific user.
   * </p>
   * <p>
   * Validates that the user and policies exist, checks permissions,
   * and removes the specified policy-user associations.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void userPolicyDelete(Long userId, Set<Long> policyIds) {
    new BizTemplate<Void>() {
      User userDb;
      List<AuthPolicy> policiesDb;

      @Override
      protected void checkParams() {
        // Verify user exists
        userDb = userQuery.checkAndFind(userId);
        // Verify policies exist
        policiesDb = authPolicyQuery.checkAndFindTenantPolicy(policyIds, true, true);
        // Validate policy permissions
        authPolicyQuery.checkAuthPolicyPermission(policyIds);
      }

      @Override
      protected Void process() {
        authPolicyOrgRepo.deleteByOrgIdAndOrgTypeAndPolicyIdIn(userId,
            AuthOrgType.USER.getValue(), policyIds);

        operationLogCmd.add(USER, userDb, DELETE_USER_POLICY,
            policiesDb.stream().map(AuthPolicy::getName).collect(Collectors.joining(",")));
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Batch removes user-policy associations.
   * </p>
   * <p>
   * Used when deleting users or policies to clean up all related associations.
   * Skips permission checks as this is typically called during cleanup operations.
   * </p>
   * <p>
   * If policyIds is empty, removes all policy associations for the specified users.
   * Otherwise, removes only the specified policy associations.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void userPolicyDeleteBatch(HashSet<Long> userIds, HashSet<Long> policyIds) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        // NOOP:: Skip permission checks for cleanup operations
        // UC deletes policies when deleting users, and does not check permissions.
      }

      @Override
      protected Void process() {
        if (isEmpty(policyIds)) {
          // Remove all policy associations for the specified users
          authPolicyOrgRepo.deleteByOrgIdInAndOrgType(userIds, AuthOrgType.USER.getValue());
        } else {
          // Remove specific policy associations for the specified users
          authPolicyOrgRepo.deleteByOrgIdInAndOrgTypeAndPolicyIdIn(userIds,
              AuthOrgType.USER.getValue(), policyIds);
        }
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Completes authorization information for organization-based policy associations.
   * </p>
   * <p>
   * Sets the policy type and application ID for each organization-policy association
   * based on the provided policy information.
   * </p>
   */
  public static void assembleOrgAuthInfo(List<AuthPolicyOrg> policyOrg, AuthPolicy authPolicyDb) {
    for (AuthPolicyOrg policyOrg0 : policyOrg) {
      policyOrg0.setPolicyType(authPolicyDb.getType()).setAppId(authPolicyDb.getAppId());
    }
  }

  /**
   * <p>
   * Completes authorization information for policy-based organization associations.
   * </p>
   * <p>
   * Sets the policy type and application ID for each policy-organization association
   * based on the provided policy information.
   * </p>
   */
  public static void assemblePolicyAuthInfo(List<AuthPolicyOrg> policyOrg,
      List<AuthPolicy> authPoliciesDb) {
    Map<Long, AuthPolicy> authPolicyMap = authPoliciesDb.stream()
        .collect(Collectors.toMap(AuthPolicy::getId, x -> x));
    for (AuthPolicyOrg policyOrg0 : policyOrg) {
      policyOrg0.setPolicyType(authPolicyMap.get(policyOrg0.getPolicyId()).getType())
          .setAppId(authPolicyMap.get(policyOrg0.getPolicyId()).getAppId());
    }
  }

  @Override
  protected BaseRepository<AuthPolicyOrg, Long> getRepository() {
    return this.authPolicyOrgRepo;
  }
}
