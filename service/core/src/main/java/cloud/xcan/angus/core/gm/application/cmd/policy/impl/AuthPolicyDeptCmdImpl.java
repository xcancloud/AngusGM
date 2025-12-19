package cloud.xcan.angus.core.gm.application.cmd.policy.impl;

import static cloud.xcan.angus.core.gm.application.cmd.policy.impl.AuthPolicyUserCmdImpl.assembleOrgAuthInfo;
import static cloud.xcan.angus.core.gm.application.cmd.policy.impl.AuthPolicyUserCmdImpl.assemblePolicyAuthInfo;
import static cloud.xcan.angus.core.gm.domain.operation.OperationResourceType.DEPT;
import static cloud.xcan.angus.core.gm.domain.operation.OperationResourceType.POLICY;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.ADD_DEPT_POLICY;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.ADD_POLICY_DEPT;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.DELETE_DEPT_POLICY;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.DELETE_POLICY_DEPT;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static java.util.Collections.singleton;

import cloud.xcan.angus.api.commonlink.AuthOrgType;
import cloud.xcan.angus.api.commonlink.dept.Dept;
import cloud.xcan.angus.api.commonlink.tag.OrgTargetType;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.operation.OperationLogCmd;
import cloud.xcan.angus.core.gm.application.cmd.policy.AuthPolicyDeptCmd;
import cloud.xcan.angus.core.gm.application.query.dept.DeptQuery;
import cloud.xcan.angus.core.gm.application.query.policy.AuthPolicyQuery;
import cloud.xcan.angus.core.gm.domain.policy.AuthPolicy;
import cloud.xcan.angus.core.gm.domain.policy.org.AuthPolicyOrg;
import cloud.xcan.angus.core.gm.domain.policy.org.AuthPolicyOrgRepo;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * Implementation of department authorization policy command operations.
 * </p>
 * <p>
 * Manages the association between departments and authorization policies, providing bidirectional
 * operations for adding and removing policy-department relationships.
 * </p>
 * <p>
 * Supports both policy-centric and department-centric operations with proper validation and audit
 * logging.
 * </p>
 */
@org.springframework.stereotype.Service
@Slf4j
public class AuthPolicyDeptCmdImpl extends CommCmd<AuthPolicyOrg, Long> implements
    AuthPolicyDeptCmd {

  @Resource
  private AuthPolicyOrgRepo authPolicyOrgRepo;
  @Resource
  private AuthPolicyQuery authPolicyQuery;
  @Resource
  private DeptQuery deptQuery;
  @Resource
  private OperationLogCmd operationLogCmd;

  /**
   * <p>
   * Associates departments with a specific authorization policy.
   * </p>
   * <p>
   * Validates that the policy and departments exist, checks permissions, and prevents duplicate
   * associations.
   * </p>
   * <p>
   * Only creates new associations for departments that aren't already associated with the policy.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> policyDeptAdd(Long policyId, List<AuthPolicyOrg> policyDept) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      Set<Long> deptIds;
      AuthPolicy policyDb;
      List<Dept> deptDb;

      @Override
      protected void checkParams() {
        // Verify policy exists and is accessible
        policyDb = authPolicyQuery.checkAndFindTenantPolicy(policyId, true, true);
        // Verify departments exist
        deptIds = policyDept.stream().map(AuthPolicyOrg::getOrgId).collect(Collectors.toSet());
        deptDb = deptQuery.checkAndFind(deptIds);
        // Validate policy permissions
        authPolicyQuery.checkAuthPolicyPermission(policyDb.getAppId(), singleton(policyId));
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        // Remove already authorized departments to prevent duplicates
        List<Long> existedDeptIds = authPolicyOrgRepo.findOrgIdsByPolicyIdAndOrgTypeAndOrgIdIn(
            policyId, OrgTargetType.DEPT.getValue(), deptIds);
        deptIds.removeAll(existedDeptIds);

        if (isNotEmpty(deptIds)) {
          // Complete authorization information
          assembleOrgAuthInfo(policyDept, policyDb);

          // Save new department authorizations
          List<AuthPolicyOrg> newPolicyDept = policyDept.stream()
              .filter(x -> deptIds.contains(x.getOrgId())).collect(Collectors.toList());
          if (isNotEmpty(newPolicyDept)) {
            List<IdKey<Long, Object>> idKeys = batchInsert(newPolicyDept);

            operationLogCmd.add(POLICY, policyDb, ADD_POLICY_DEPT,
                deptDb.stream().map(Dept::getName).collect(Collectors.joining(",")));
            return idKeys;
          }
        }
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Removes department associations from a specific authorization policy.
   * </p>
   * <p>
   * Validates that the policy and departments exist, checks permissions, and removes the specified
   * department-policy associations.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void policyDeptDelete(Long policyId, Set<Long> deptIds) {
    new BizTemplate<Void>() {
      AuthPolicy policyDb;
      List<Dept> deptDb;

      @Override
      protected void checkParams() {
        // Verify policy exists
        policyDb = authPolicyQuery.checkAndFindTenantPolicy(policyId, false, false);
        // Verify departments exist
        deptDb = deptQuery.checkAndFind(deptIds);
        // Validate policy permissions
        authPolicyQuery.checkAuthPolicyPermission(policyDb.getAppId(), singleton(policyId));
      }

      @Override
      protected Void process() {
        authPolicyOrgRepo.deleteByPolicyIdAndOrgTypeAndOrgIdIn(policyId,
            AuthOrgType.DEPT.getValue(), deptIds);

        operationLogCmd.add(POLICY, policyDb, DELETE_POLICY_DEPT,
            deptDb.stream().map(Dept::getName).collect(Collectors.joining(",")));
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Associates authorization policies with a specific department.
   * </p>
   * <p>
   * Validates that the department and policies exist, checks permissions, and prevents duplicate
   * associations.
   * </p>
   * <p>
   * Only creates new associations for policies that aren't already associated with the department.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> deptPolicyAdd(Long deptId, List<AuthPolicyOrg> deptPolices) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      Dept deptDb;
      Set<Long> policyIds;
      List<AuthPolicy> policiesDb;

      @Override
      protected void checkParams() {
        // Verify department exists
        deptDb = deptQuery.checkAndFind(deptId);
        // Verify policies exist
        policyIds = deptPolices.stream().map(AuthPolicyOrg::getPolicyId)
            .collect(Collectors.toSet());
        policiesDb = authPolicyQuery.checkAndFindTenantPolicy(policyIds, true, true);
        // Validate policy permissions
        authPolicyQuery.checkAuthPolicyPermission(policiesDb);
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        // Remove already authorized policies to prevent duplicates
        List<Long> existedPolicyIds = authPolicyOrgRepo
            .findPolicyIdsByUserIdAndOrgTypeAndPolicyIdIn(deptId, OrgTargetType.DEPT.getValue(),
                policyIds);
        policyIds.removeAll(existedPolicyIds);

        if (isNotEmpty(policyIds)) {
          // Complete authorization information
          assemblePolicyAuthInfo(deptPolices, policiesDb);

          // Save new policy authorizations
          List<AuthPolicyOrg> addDeptPolices = deptPolices.stream()
              .filter(x -> policyIds.contains(x.getPolicyId())).collect(Collectors.toList());
          if (isNotEmpty(addDeptPolices)) {
            List<IdKey<Long, Object>> idKeys = batchInsert(addDeptPolices);

            operationLogCmd.add(DEPT, deptDb, ADD_DEPT_POLICY,
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
   * Removes authorization policy associations from a specific department.
   * </p>
   * <p>
   * Validates that the department and policies exist, checks permissions, and removes the specified
   * policy-department associations.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void deptPolicyDelete(Long deptId, Set<Long> policyIds) {
    new BizTemplate<Void>() {
      Dept deptDb;
      List<AuthPolicy> policiesDb;

      @Override
      protected void checkParams() {
        // Verify department exists
        deptDb = deptQuery.checkAndFind(deptId);
        // Verify policies exist
        policiesDb = authPolicyQuery.checkAndFindTenantPolicy(policyIds, true, true);
        // Validate policy permissions
        authPolicyQuery.checkAuthPolicyPermission(policyIds);
      }

      @Override
      protected Void process() {
        authPolicyOrgRepo.deleteByOrgIdAndOrgTypeAndPolicyIdIn(deptId,
            AuthOrgType.DEPT.getValue(), policyIds);

        operationLogCmd.add(DEPT, deptDb, DELETE_DEPT_POLICY,
            policiesDb.stream().map(AuthPolicy::getName).collect(Collectors.joining(",")));
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Batch removes department-policy associations.
   * </p>
   * <p>
   * Used when deleting departments or policies to clean up all related associations. Skips
   * permission checks as this is typically called during cleanup operations.
   * </p>
   * <p>
   * If policyIds is empty, removes all policy associations for the specified departments.
   * Otherwise, removes only the specified policy associations.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void deptPolicyDeleteBatch(HashSet<Long> deptIds, HashSet<Long> policyIds) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        // NOOP:: Skip permission checks for cleanup operations
        // UC deletes policies when deleting departments, and does not check permissions.
      }

      @Override
      protected Void process() {
        if (isEmpty(policyIds)) {
          // Remove all policy associations for the specified departments
          authPolicyOrgRepo.deleteByOrgIdInAndOrgType(deptIds, AuthOrgType.DEPT.getValue());
        } else {
          // Remove specific policy associations for the specified departments
          authPolicyOrgRepo.deleteByOrgIdInAndOrgTypeAndPolicyIdIn(deptIds,
              AuthOrgType.DEPT.getValue(), policyIds);
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
