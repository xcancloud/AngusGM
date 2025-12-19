package cloud.xcan.angus.core.gm.application.cmd.policy.impl;

import static cloud.xcan.angus.core.gm.domain.operation.OperationResourceType.POLICY;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.ADD_POLICY_FUNC;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.DELETE_POLICY_FUNC;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.UPDATE_POLICY_FUNC;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;

import cloud.xcan.angus.api.commonlink.app.func.AppFunc;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.operation.OperationLogCmd;
import cloud.xcan.angus.core.gm.application.cmd.policy.AuthPolicyFuncCmd;
import cloud.xcan.angus.core.gm.application.query.app.AppFuncQuery;
import cloud.xcan.angus.core.gm.application.query.policy.AuthPolicyFuncQuery;
import cloud.xcan.angus.core.gm.application.query.policy.AuthPolicyQuery;
import cloud.xcan.angus.core.gm.domain.policy.AuthPolicy;
import cloud.xcan.angus.core.gm.domain.policy.func.AuthPolicyFunc;
import cloud.xcan.angus.core.gm.domain.policy.func.AuthPolicyFuncRepo;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * Implementation of authorization policy function command operations.
 * </p>
 * <p>
 * Manages the association between authorization policies and application functions, providing
 * operations to add, replace, and delete function-policy relationships.
 * </p>
 * <p>
 * Supports hierarchical function authorization where parent functions are automatically authorized
 * when child functions are authorized.
 * </p>
 */
@org.springframework.stereotype.Service
@Slf4j
public class AuthPolicyFuncCmdImpl extends CommCmd<AuthPolicyFunc, Long> implements
    AuthPolicyFuncCmd {

  @Resource
  private AuthPolicyFuncRepo authPolicyFuncRepo;
  @Resource
  private AuthPolicyFuncQuery authPolicyFuncQuery;
  @Resource
  private AppFuncQuery appFuncQuery;
  @Resource
  private AuthPolicyQuery authPolicyQuery;
  @Resource
  private OperationLogCmd operationLogCmd;

  /**
   * <p>
   * Associates application functions with an authorization policy.
   * </p>
   * <p>
   * Validates that the policy and functions exist, checks permissions, and prevents duplicate
   * associations.
   * </p>
   * <p>
   * When a child function is authorized, the parent function is also authorized automatically to
   * maintain hierarchical consistency.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void add(Long policyId, Set<Long> appFuncIds) {
    new BizTemplate<Void>() {
      List<AppFunc> funcDb;
      AuthPolicy policyDb;

      @Override
      protected void checkParams() {
        // Verify policy exists
        policyDb = authPolicyQuery.checkAndFind(policyId, false, true);

        // Validate permissions for predefined policies on operation client
        authPolicyQuery.checkOpPolicyPermission(singletonList(policyDb));

        // Verify application functions exist
        if (isNotEmpty(appFuncIds)) {
          funcDb = appFuncQuery.checkAndFind(policyDb.getAppId(), appFuncIds, true);
        }
      }

      @Override
      protected Void process() {
        Set<Long> existedFuncIds = authPolicyFuncQuery.findExistedFuncIdsByPolicyId(policyId);
        if (isEmpty(existedFuncIds)) {
          // No existing functions, add all new functions
          policyDb.setPolicyFunc(appFuncIds.stream()
              .map(x -> new AuthPolicyFunc().setFuncId(x)).collect(Collectors.toList()));
          add0(singletonList(policyDb), funcDb);
          return null;
        }

        // Add only functions that don't already exist
        List<AppFunc> notExistedFuncDb = funcDb.stream()
            .filter(x -> !existedFuncIds.contains(x.getId())).collect(Collectors.toList());
        if (isNotEmpty(notExistedFuncDb)) {
          policyDb.setPolicyFunc(notExistedFuncDb.stream()
              .map(x -> new AuthPolicyFunc().setFuncId(x.getId())).collect(Collectors.toList()));
          add0(singletonList(policyDb), notExistedFuncDb);
        }

        operationLogCmd.add(POLICY, policyDb, ADD_POLICY_FUNC,
            funcDb.stream().map(AppFunc::getName).collect(Collectors.joining(",")));
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Replaces all function associations for an authorization policy.
   * </p>
   * <p>
   * Validates that the policy and functions exist, checks permissions, and completely replaces
   * existing function associations.
   * </p>
   * <p>
   * When a child function is authorized, the parent function is also authorized automatically to
   * maintain hierarchical consistency.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void replace(Long policyId, Set<Long> appFuncIds) {
    new BizTemplate<Void>() {
      AuthPolicy policyDb;
      List<AppFunc> funcDb;

      @Override
      protected void checkParams() {
        // Verify policy exists
        policyDb = authPolicyQuery.checkAndFind(policyId, false, true);

        // Validate permissions for predefined policies on operation client
        authPolicyQuery.checkOpPolicyPermission(singletonList(policyDb));

        // Verify application functions exist
        if (isNotEmpty(appFuncIds)) {
          funcDb = appFuncQuery.checkAndFind(policyDb.getAppId(), appFuncIds, true);
        }
      }

      @Override
      protected Void process() {
        // Clear all existing function associations when appFuncIds is empty
        if (isEmpty(appFuncIds)) {
          authPolicyFuncRepo.deleteByPolicyIdIn(singleton(policyId));
          return null;
        }

        // Replace all function associations
        policyDb.setPolicyFunc(funcDb.stream()
            .map(x -> new AuthPolicyFunc().setFuncId(x.getId())).collect(Collectors.toList()));
        replace0(singletonList(policyDb), funcDb);

        operationLogCmd.add(POLICY, policyDb, UPDATE_POLICY_FUNC, isNull(funcDb)
            ? new Object[]{""}
            : funcDb.stream().map(AppFunc::getName).collect(Collectors.joining(",")));
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Removes specific function associations from an authorization policy.
   * </p>
   * <p>
   * Validates that the policy and functions exist, checks permissions, and removes the specified
   * function-policy associations.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(Long policyId, Set<Long> appFuncIds) {
    new BizTemplate<Void>() {
      AuthPolicy policyDb;
      List<AppFunc> funcDb;

      @Override
      protected void checkParams() {
        // Verify policy exists
        policyDb = authPolicyQuery.checkAndFind(policyId, false, false);
        // Verify application functions exist
        funcDb = appFuncQuery.checkAndFind(policyDb.getAppId(), appFuncIds, true);
        // Validate permissions for predefined policies on operation client
        authPolicyQuery.checkOpPolicyPermission(singletonList(policyDb));
      }

      @Override
      protected Void process() {
        authPolicyFuncRepo.deleteByPolicyIdAndFuncIdIn(policyId, appFuncIds);

        operationLogCmd.add(POLICY, policyDb, DELETE_POLICY_FUNC,
            funcDb.stream().map(AppFunc::getName).collect(Collectors.joining(",")));
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Associates application functions with multiple authorization policies.
   * </p>
   * <p>
   * Internal method used by other operations to batch create function-policy associations. Sets up
   * the complete authorization information including app ID and function type.
   * </p>
   * <p>
   * When a child function is authorized, the parent function is also authorized automatically to
   * maintain hierarchical consistency.
   * </p>
   */
  @Override
  public List<IdKey<Long, Object>> add0(List<AuthPolicy> policies, List<AppFunc> appFuncDb) {
    Map<Long, AppFunc> appFuncMap = appFuncDb.stream()
        .collect(Collectors.toMap(AppFunc::getId, x -> x));
    List<AuthPolicyFunc> policyFunc = new ArrayList<>();
    for (AuthPolicy policy : policies) {
      if (policy.hasFunc()) {
        for (AuthPolicyFunc func : policy.getPolicyFunc()) {
          func.setPolicyId(policy.getId())
              .setAppId(appFuncMap.get(func.getFuncId()).getAppId())
              .setFuncType(appFuncMap.get(func.getFuncId()).getType());
          policyFunc.add(func);
        }
      }
    }
    return batchInsert(policyFunc);
  }

  /**
   * <p>
   * Replaces all function associations for multiple authorization policies.
   * </p>
   * <p>
   * Internal method used by other operations to batch replace function-policy associations. First
   * removes all existing associations, then creates new ones.
   * </p>
   */
  @Override
  public void replace0(List<AuthPolicy> policies, List<AppFunc> appFuncDb) {
    authPolicyFuncRepo.deleteByPolicyIdIn(policies.stream().map(AuthPolicy::getId)
        .collect(Collectors.toSet()));
    add0(policies, appFuncDb);
  }

  @Override
  protected BaseRepository<AuthPolicyFunc, Long> getRepository() {
    return this.authPolicyFuncRepo;
  }
}
