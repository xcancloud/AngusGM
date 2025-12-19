package cloud.xcan.angus.core.gm.application.cmd.policy.impl;

import static cloud.xcan.angus.core.gm.application.cmd.policy.impl.AuthPolicyUserCmdImpl.assembleOrgAuthInfo;
import static cloud.xcan.angus.core.gm.application.cmd.policy.impl.AuthPolicyUserCmdImpl.assemblePolicyAuthInfo;
import static cloud.xcan.angus.core.gm.domain.operation.OperationResourceType.GROUP;
import static cloud.xcan.angus.core.gm.domain.operation.OperationResourceType.POLICY;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.ADD_GROUP_POLICY;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.ADD_POLICY_GROUP;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.DELETE_GROUP_POLICY;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.DELETE_POLICY_GROUP;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static java.util.Collections.singleton;

import cloud.xcan.angus.api.commonlink.AuthOrgType;
import cloud.xcan.angus.api.commonlink.group.Group;
import cloud.xcan.angus.api.commonlink.tag.OrgTargetType;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.operation.OperationLogCmd;
import cloud.xcan.angus.core.gm.application.cmd.policy.AuthPolicyGroupCmd;
import cloud.xcan.angus.core.gm.application.query.group.GroupQuery;
import cloud.xcan.angus.core.gm.application.query.policy.AuthPolicyQuery;
import cloud.xcan.angus.core.gm.domain.policy.AuthPolicy;
import cloud.xcan.angus.core.gm.domain.policy.org.AuthPolicyOrg;
import cloud.xcan.angus.core.gm.domain.policy.org.AuthPolicyOrgRepo;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * Implementation of group authorization policy command operations.
 * </p>
 * <p>
 * Manages the association between groups and authorization policies, providing bidirectional
 * operations for adding and removing policy-group relationships.
 * </p>
 * <p>
 * Supports both policy-centric and group-centric operations with proper validation and audit
 * logging.
 * </p>
 */
@org.springframework.stereotype.Service
@Slf4j
public class AuthPolicyGroupCmdImpl extends CommCmd<AuthPolicyOrg, Long> implements
    AuthPolicyGroupCmd {

  @Resource
  private AuthPolicyOrgRepo authPolicyOrgRepo;
  @Resource
  private AuthPolicyQuery authPolicyQuery;
  @Resource
  private GroupQuery groupQuery;
  @Resource
  private OperationLogCmd operationLogCmd;

  /**
   * <p>
   * Associates groups with a specific authorization policy.
   * </p>
   * <p>
   * Validates that the policy and groups exist, checks permissions, and prevents duplicate
   * associations.
   * </p>
   * <p>
   * Only creates new associations for groups that aren't already associated with the policy.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> policyGroupAdd(Long policyId, List<AuthPolicyOrg> policyGroups) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      Set<Long> groupIds;
      List<Group> groupsDb;
      AuthPolicy policyDb;

      @Override
      protected void checkParams() {
        // Verify policy exists and is accessible
        policyDb = authPolicyQuery.checkAndFindTenantPolicy(policyId, true, true);
        // Verify groups exist and are valid
        groupIds = policyGroups.stream().map(AuthPolicyOrg::getOrgId).collect(Collectors.toSet());
        groupsDb = groupQuery.checkValidAndFind(groupIds);
        // Validate policy permissions
        authPolicyQuery.checkAuthPolicyPermission(policyDb.getAppId(), singleton(policyId));
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        // Remove already authorized groups to prevent duplicates
        List<Long> existedGroupIds = authPolicyOrgRepo.findOrgIdsByPolicyIdAndOrgTypeAndOrgIdIn(
            policyId, OrgTargetType.GROUP.getValue(), groupIds);
        groupIds.removeAll(existedGroupIds);

        if (isNotEmpty(groupIds)) {
          // Complete authorization information
          assembleOrgAuthInfo(policyGroups, policyDb);

          // Save new group authorizations
          List<AuthPolicyOrg> newPolicyGroups = policyGroups.stream()
              .filter(x -> groupIds.contains(x.getOrgId())).collect(Collectors.toList());
          if (isNotEmpty(newPolicyGroups)) {
            List<IdKey<Long, Object>> idKeys = batchInsert(newPolicyGroups);

            operationLogCmd.add(POLICY, policyDb, ADD_POLICY_GROUP,
                groupsDb.stream().map(Group::getName).collect(Collectors.joining(",")));
            return idKeys;
          }
        }
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Removes group associations from a specific authorization policy.
   * </p>
   * <p>
   * Validates that the policy and groups exist, checks permissions, and removes the specified
   * group-policy associations.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void policyGroupDelete(Long policyId, Set<Long> groupIds) {
    new BizTemplate<Void>() {
      AuthPolicy policyDb;
      List<Group> groupsDb;

      @Override
      protected void checkParams() {
        // Verify policy exists
        policyDb = authPolicyQuery.checkAndFindTenantPolicy(policyId, false, false);
        // Verify groups exist
        groupsDb = groupQuery.checkAndFind(groupIds);
        // Validate policy permissions
        authPolicyQuery.checkAuthPolicyPermission(policyDb.getAppId(), singleton(policyId));
      }

      @Override
      protected Void process() {
        authPolicyOrgRepo.deleteByPolicyIdAndOrgTypeAndOrgIdIn(policyId,
            AuthOrgType.GROUP.getValue(), groupIds);

        operationLogCmd.add(POLICY, policyDb, DELETE_POLICY_GROUP,
            groupsDb.stream().map(Group::getName).collect(Collectors.joining(",")));
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Associates authorization policies with a specific group.
   * </p>
   * <p>
   * Validates that the group and policies exist, checks permissions, and prevents duplicate
   * associations.
   * </p>
   * <p>
   * Only creates new associations for policies that aren't already associated with the group.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> groupPolicyAdd(Long groupId, List<AuthPolicyOrg> groupPolices) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      Group groupDb;
      Set<Long> policyIds;
      List<AuthPolicy> policiesDb;

      @Override
      protected void checkParams() {
        // Verify group exists and is valid
        groupDb = groupQuery.checkValidAndFind(groupId);
        // Verify policies exist
        policyIds = groupPolices.stream().map(AuthPolicyOrg::getPolicyId)
            .collect(Collectors.toSet());
        policiesDb = authPolicyQuery.checkAndFindTenantPolicy(policyIds, true, true);
        // Validate policy permissions
        authPolicyQuery.checkAuthPolicyPermission(policiesDb);
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        // Remove already authorized policies to prevent duplicates
        List<Long> existedPolicyIds = authPolicyOrgRepo
            .findPolicyIdsByUserIdAndOrgTypeAndPolicyIdIn(groupId, OrgTargetType.GROUP.getValue(),
                policyIds);
        policyIds.removeAll(existedPolicyIds);

        if (isNotEmpty(policyIds)) {
          // Complete authorization information
          assemblePolicyAuthInfo(groupPolices, policiesDb);

          // Save new policy authorizations
          List<AuthPolicyOrg> newGroupPolices = groupPolices.stream()
              .filter(x -> policyIds.contains(x.getPolicyId())).collect(Collectors.toList());
          if (isNotEmpty(newGroupPolices)) {
            List<IdKey<Long, Object>> idKeys = batchInsert(newGroupPolices);

            operationLogCmd.add(GROUP, groupDb, ADD_GROUP_POLICY,
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
   * Removes authorization policy associations from a specific group.
   * </p>
   * <p>
   * Validates that the group and policies exist, checks permissions, and removes the specified
   * policy-group associations.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void groupPolicyDelete(Long groupId, Set<Long> policyIds) {
    new BizTemplate<Void>() {
      Group groupDb;
      List<AuthPolicy> policiesDb;

      @Override
      protected void checkParams() {
        // Verify group exists and is valid
        groupDb = groupQuery.checkValidAndFind(groupId);
        // Verify policies exist
        policiesDb = authPolicyQuery.checkAndFindTenantPolicy(policyIds, true, true);
        // Validate policy permissions
        authPolicyQuery.checkAuthPolicyPermission(policyIds);
      }

      @Override
      protected Void process() {
        authPolicyOrgRepo.deleteByOrgIdAndOrgTypeAndPolicyIdIn(groupId,
            AuthOrgType.GROUP.getValue(), policyIds);

        operationLogCmd.add(GROUP, groupDb, DELETE_GROUP_POLICY,
            policiesDb.stream().map(AuthPolicy::getName).collect(Collectors.joining(",")));
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Batch removes group-policy associations.
   * </p>
   * <p>
   * Used when deleting groups or policies to clean up all related associations. Skips permission
   * checks as this is typically called during cleanup operations.
   * </p>
   * <p>
   * If policyIds is empty, removes all policy associations for the specified groups. Otherwise,
   * removes only the specified policy associations.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void groupPolicyDeleteBatch(@NotEmpty HashSet<Long> groupIds, HashSet<Long> policyIds) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        // NOOP:: Skip permission checks for cleanup operations
        // UC deletes policies when deleting groups, and does not check permissions.
      }

      @Override
      protected Void process() {
        if (isEmpty(policyIds)) {
          // Remove all policy associations for the specified groups
          authPolicyOrgRepo.deleteByOrgIdInAndOrgType(groupIds, AuthOrgType.GROUP.getValue());
        } else {
          // Remove specific policy associations for the specified groups
          authPolicyOrgRepo.deleteByOrgIdInAndOrgTypeAndPolicyIdIn(groupIds,
              AuthOrgType.GROUP.getValue(), policyIds);
        }
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<AuthPolicyOrg, Long> getRepository() {
    return this.authPolicyOrgRepo;
  }
}
