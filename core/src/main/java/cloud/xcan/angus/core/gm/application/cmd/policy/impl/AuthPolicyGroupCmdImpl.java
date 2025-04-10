package cloud.xcan.angus.core.gm.application.cmd.policy.impl;

import static cloud.xcan.angus.core.gm.application.cmd.policy.impl.AuthPolicyUserCmdImpl.assembleOrgAuthInfo;
import static cloud.xcan.angus.core.gm.application.cmd.policy.impl.AuthPolicyUserCmdImpl.assemblePolicyAuthInfo;
import static cloud.xcan.angus.core.gm.domain.operation.OperationResourceType.GROUP;
import static cloud.xcan.angus.core.gm.domain.operation.OperationResourceType.POLICY;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.ADD_GROUP_POLICY;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.ADD_POLICY_GROUP;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.DELETED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.DELETE_GROUP_POLICY;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.DELETE_POLICY_GROUP;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static java.util.Collections.singleton;

import cloud.xcan.angus.api.commonlink.AuthOrgType;
import cloud.xcan.angus.api.commonlink.group.Group;
import cloud.xcan.angus.api.commonlink.tag.OrgTargetType;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.operation.OperationLogCmd;
import cloud.xcan.angus.core.gm.application.cmd.policy.AuthPolicyGroupCmd;
import cloud.xcan.angus.core.gm.application.query.group.GroupQuery;
import cloud.xcan.angus.core.gm.application.query.policy.AuthPolicyQuery;
import cloud.xcan.angus.core.gm.domain.operation.OperationResourceType;
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


@Biz
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

  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> policyGroupAdd(Long policyId, List<AuthPolicyOrg> policyGroups) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      Set<Long> groupIds;
      List<Group> groupsDb;
      AuthPolicy policyDb;

      @Override
      protected void checkParams() {
        // Check the policy existed
        policyDb = authPolicyQuery.checkAndFindTenantPolicy(policyId, true, true);
        // Check the groups existed
        groupIds = policyGroups.stream().map(AuthPolicyOrg::getOrgId).collect(Collectors.toSet());
        groupsDb = groupQuery.checkValidAndFind(groupIds);
        // Check the policy permission
        authPolicyQuery.checkAuthPolicyPermission(policyDb.getAppId(), singleton(policyId));
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        // De-duplication of authorized groups
        List<Long> existedGroupIds = authPolicyOrgRepo.findOrgIdsByPolicyIdAndOrgTypeAndOrgIdIn(
            policyId, OrgTargetType.GROUP.getValue(), groupIds);
        groupIds.removeAll(existedGroupIds);

        if (isNotEmpty(groupIds)) {
          // Complete authorization information
          assembleOrgAuthInfo(policyGroups, policyDb);

          // Save nonexistent authorization
          List<AuthPolicyOrg> newPolicyGroups = policyGroups.stream()
              .filter(x -> groupIds.contains(x.getOrgId())).collect(Collectors.toList());
          if (isNotEmpty(newPolicyGroups)) {
            List<IdKey<Long, Object>> idKeys = batchInsert(newPolicyGroups);

            operationLogCmd.add(POLICY, policyDb, ADD_POLICY_GROUP,
                groupsDb.stream().map(Group::getName).toList().toArray());
            return idKeys;
          }
        }
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void policyGroupDelete(Long policyId, Set<Long> groupIds) {
    new BizTemplate<Void>() {
      AuthPolicy policyDb;
      List<Group> groupsDb;

      @Override
      protected void checkParams() {
        // Check the policy existed
        policyDb = authPolicyQuery.checkAndFindTenantPolicy(policyId, false, false);
        // Check the groups existed
        groupsDb = groupQuery.checkAndFind(groupIds);
        // Check the policy permission
        authPolicyQuery.checkAuthPolicyPermission(policyDb.getAppId(), singleton(policyId));
      }

      @Override
      protected Void process() {
        authPolicyOrgRepo.deleteByPolicyIdAndOrgTypeAndOrgIdIn(policyId,
            AuthOrgType.GROUP.getValue(), groupIds);

        operationLogCmd.add(POLICY, policyDb, DELETE_POLICY_GROUP,
            groupsDb.stream().map(Group::getName).toList().toArray());
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> groupPolicyAdd(Long groupId, List<AuthPolicyOrg> groupPolices) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      Group groupDb;
      Set<Long> policyIds;
      List<AuthPolicy> policiesDb;

      @Override
      protected void checkParams() {
        // Check the group existed
        groupDb = groupQuery.checkValidAndFind(groupId);
        // Check the policies existed
        policyIds = groupPolices.stream().map(AuthPolicyOrg::getPolicyId)
            .collect(Collectors.toSet());
        policiesDb = authPolicyQuery.checkAndFindTenantPolicy(policyIds, true, true);
        // Check the policy permission
        authPolicyQuery.checkAuthPolicyPermission(policiesDb);
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        // De-duplication of authorized polices
        List<Long> existedPolicyIds = authPolicyOrgRepo
            .findPolicyIdsByUserIdAndOrgTypeAndPolicyIdIn(groupId, OrgTargetType.GROUP.getValue(),
                policyIds);
        policyIds.removeAll(existedPolicyIds);

        if (isNotEmpty(policyIds)) {
          // Complete authorization information
          assemblePolicyAuthInfo(groupPolices, policiesDb);

          // Save nonexistent authorization
          List<AuthPolicyOrg> newGroupPolices = groupPolices.stream()
              .filter(x -> policyIds.contains(x.getPolicyId())).collect(Collectors.toList());
          if (isNotEmpty(newGroupPolices)) {
            List<IdKey<Long, Object>> idKeys = batchInsert(newGroupPolices);

            operationLogCmd.add(GROUP, groupDb, ADD_GROUP_POLICY,
                policiesDb.stream().map(AuthPolicy::getName).toList().toArray());
            return idKeys;
          }
        }
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void groupPolicyDelete(Long groupId, Set<Long> policyIds) {
    new BizTemplate<Void>() {
      Group groupDb;
      Set<Long> policyIds;
      List<AuthPolicy> policiesDb;

      @Override
      protected void checkParams() {
        // Check the group existed
        groupDb = groupQuery.checkValidAndFind(groupId);
        // Check the policies existed
        policiesDb = authPolicyQuery.checkAndFindTenantPolicy(policyIds, true, true);
        // Check the policy permission
        authPolicyQuery.checkAuthPolicyPermission(policyIds);
      }

      @Override
      protected Void process() {
        authPolicyOrgRepo.deleteByOrgIdAndOrgTypeAndPolicyIdIn(groupId,
            AuthOrgType.GROUP.getValue(), policyIds);

        operationLogCmd.add(GROUP, groupDb, DELETE_GROUP_POLICY,
            policiesDb.stream().map(AuthPolicy::getName).toList().toArray());
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void groupPolicyDeleteBatch(@NotEmpty HashSet<Long> groupIds, HashSet<Long> policyIds) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        // NOOP:: Check the policy permission <- UC deletes policies when deleting groups, and does not check permissions.
      }

      @Override
      protected Void process() {
        if (isEmpty(policyIds)) {
          authPolicyOrgRepo.deleteByOrgIdInAndOrgType(groupIds, AuthOrgType.GROUP.getValue());
        } else {
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
