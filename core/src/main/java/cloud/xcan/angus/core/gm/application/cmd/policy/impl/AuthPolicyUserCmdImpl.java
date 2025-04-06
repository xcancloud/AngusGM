package cloud.xcan.angus.core.gm.application.cmd.policy.impl;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static java.util.Collections.singleton;

import cloud.xcan.angus.api.commonlink.AuthOrgType;
import cloud.xcan.angus.api.commonlink.tag.OrgTargetType;
import cloud.xcan.angus.api.manager.UserManager;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.policy.AuthPolicyUserCmd;
import cloud.xcan.angus.core.gm.application.query.policy.AuthPolicyQuery;
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


@Biz
@Slf4j
public class AuthPolicyUserCmdImpl extends CommCmd<AuthPolicyOrg, Long> implements
    AuthPolicyUserCmd {

  @Resource
  private AuthPolicyOrgRepo authPolicyOrgRepo;

  @Resource
  private AuthPolicyQuery authPolicyQuery;

  @Resource
  private UserManager userManager;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> policyUserAdd(Long policyId, List<AuthPolicyOrg> policyUsers) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      Set<Long> userIds;
      AuthPolicy policyDb;

      @Override
      protected void checkParams() {
        // Check the policy existed
        policyDb = authPolicyQuery.checkAndFindTenantPolicy(policyId, false, false);
        // Check the users existed
        userIds = policyUsers.stream().map(AuthPolicyOrg::getOrgId).collect(Collectors.toSet());
        userManager.checkValidAndFind(userIds);
        // Check the policy permission
        authPolicyQuery.checkAuthPolicyPermission(policyDb.getAppId(), singleton(policyId));
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        // De-duplication of authorized users
        List<Long> existedUserIds = authPolicyOrgRepo.findOrgIdsByPolicyIdAndOrgTypeAndOrgIdIn(
            policyId, OrgTargetType.USER.getValue(), userIds);
        userIds.removeAll(existedUserIds);

        if (isNotEmpty(userIds)) {
          // Complete authorization information
          assembleOrgAuthInfo(policyUsers, policyDb);

          // Save nonexistent authorization
          List<AuthPolicyOrg> addPolicyUsers = policyUsers.stream()
              .filter(x -> userIds.contains(x.getOrgId())).collect(Collectors.toList());
          if (isNotEmpty(addPolicyUsers)) {
            return batchInsert(addPolicyUsers);
          }
        }
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void policyUserDelete(Long policyId, Set<Long> userIds) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        // Check the policy existed
        AuthPolicy authPolicyDb = authPolicyQuery.checkAndFindTenantPolicy(policyId, false, false);
        // Check the policy permission
        authPolicyQuery.checkAuthPolicyPermission(authPolicyDb.getAppId(), singleton(policyId));
      }

      @Override
      protected Void process() {
        authPolicyOrgRepo.deleteByPolicyIdAndOrgTypeAndOrgIdIn(policyId,
            AuthOrgType.USER.getValue(), userIds);
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> userPolicyAdd(Long userId, List<AuthPolicyOrg> userPolicies) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      Set<Long> policyIds;
      List<AuthPolicy> authPoliciesDb;

      @Override
      protected void checkParams() {
        // Check the user existed
        userManager.checkValidAndFind(userId);

        // Check the policies existed
        policyIds = userPolicies.stream().map(AuthPolicyOrg::getPolicyId)
            .collect(Collectors.toSet());
        authPoliciesDb = authPolicyQuery.checkAndFindTenantPolicy(policyIds, true, true);

        // Check the policy permission
        authPolicyQuery.checkAuthPolicyPermission(authPoliciesDb);
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        // De-duplication of authorized polices
        List<Long> existedPolicyIds = authPolicyOrgRepo.findPolicyIdsByUserIdAndOrgTypeAndPolicyIdIn(
            userId, OrgTargetType.USER.getValue(), policyIds);
        policyIds.removeAll(existedPolicyIds);

        if (isNotEmpty(policyIds)) {
          // Complete authorization information
          assemblePolicyAuthInfo(userPolicies, authPoliciesDb);

          // Save nonexistent authorization
          List<AuthPolicyOrg> newUserPolicies = userPolicies.stream()
              .filter(x -> policyIds.contains(x.getPolicyId())).collect(Collectors.toList());
          if (isNotEmpty(newUserPolicies)) {
            return batchInsert(newUserPolicies);
          }
        }
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void userPolicyDelete(Long userId, Set<Long> policyIds) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        // Check the policy permission
        authPolicyQuery.checkAuthPolicyPermission(policyIds);
      }

      @Override
      protected Void process() {
        authPolicyOrgRepo.deleteByOrgIdAndOrgTypeAndPolicyIdIn(userId,
            AuthOrgType.USER.getValue(), policyIds);
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void userPolicyDeleteBatch(HashSet<Long> userIds, HashSet<Long> policyIds) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        // NOOP:: Check the policy permission <- UC deletes policies when deleting users, and does not check permissions.
      }

      @Override
      protected Void process() {
        if (isEmpty(policyIds)) {
          authPolicyOrgRepo.deleteByOrgIdInAndOrgType(userIds, AuthOrgType.USER.getValue());
        } else {
          authPolicyOrgRepo.deleteByOrgIdInAndOrgTypeAndPolicyIdIn(userIds,
              AuthOrgType.USER.getValue(), policyIds);
        }
        return null;
      }
    }.execute();
  }

  public static void assembleOrgAuthInfo(List<AuthPolicyOrg> policyOrg, AuthPolicy authPolicyDb) {
    for (AuthPolicyOrg policyOrg0 : policyOrg) {
      policyOrg0.setPolicyType(authPolicyDb.getType()).setAppId(authPolicyDb.getAppId());
    }
  }

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
