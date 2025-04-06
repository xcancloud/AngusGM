package cloud.xcan.angus.core.gm.application.cmd.policy.impl;

import static cloud.xcan.angus.core.gm.application.cmd.policy.impl.AuthPolicyUserCmdImpl.assembleOrgAuthInfo;
import static cloud.xcan.angus.core.gm.application.cmd.policy.impl.AuthPolicyUserCmdImpl.assemblePolicyAuthInfo;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static java.util.Collections.singleton;

import cloud.xcan.angus.api.commonlink.AuthOrgType;
import cloud.xcan.angus.api.commonlink.tag.OrgTargetType;
import cloud.xcan.angus.api.manager.DeptManager;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.policy.AuthPolicyDeptCmd;
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


@Biz
@Slf4j
public class AuthPolicyDeptCmdImpl extends CommCmd<AuthPolicyOrg, Long> implements
    AuthPolicyDeptCmd {

  @Resource
  private AuthPolicyOrgRepo authPolicyOrgRepo;

  @Resource
  private AuthPolicyQuery authPolicyQuery;

  @Resource
  private DeptManager deptManager;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> policyDeptAdd(Long policyId, List<AuthPolicyOrg> policyDept) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      Set<Long> deptIds;
      AuthPolicy policyDb;

      @Override
      protected void checkParams() {
        // Check the policy existed
        policyDb = authPolicyQuery.checkAndFindTenantPolicy(policyId, true, true);
        // Check the departments existed
        deptIds = policyDept.stream().map(AuthPolicyOrg::getOrgId).collect(Collectors.toSet());
        deptManager.checkAndFind(deptIds);
        // Check the policy permission
        authPolicyQuery.checkAuthPolicyPermission(policyDb.getAppId(), singleton(policyId));
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        // De-duplication of authorized departments
        List<Long> existedDeptIds = authPolicyOrgRepo.findOrgIdsByPolicyIdAndOrgTypeAndOrgIdIn(
            policyId, OrgTargetType.DEPT.getValue(), deptIds);
        deptIds.removeAll(existedDeptIds);

        if (isNotEmpty(deptIds)) {
          // Complete authorization information
          assembleOrgAuthInfo(policyDept, policyDb);

          // Save nonexistent authorization
          List<AuthPolicyOrg> newPolicyDept = policyDept.stream()
              .filter(x -> deptIds.contains(x.getOrgId())).collect(Collectors.toList());
          if (isNotEmpty(newPolicyDept)) {
            return batchInsert(newPolicyDept);
          }
        }
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void policyDeptDelete(Long policyId, Set<Long> deptIds) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        // Check the policy existed
        AuthPolicy policyDb = authPolicyQuery.checkAndFindTenantPolicy(policyId, false, false);
        // Check the policy permission
        authPolicyQuery.checkAuthPolicyPermission(policyDb.getAppId(), singleton(policyId));
      }

      @Override
      protected Void process() {
        authPolicyOrgRepo.deleteByPolicyIdAndOrgTypeAndOrgIdIn(policyId,
            AuthOrgType.DEPT.getValue(), deptIds);
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public List<IdKey<Long, Object>> deptPolicyAdd(Long deptId, List<AuthPolicyOrg> deptPolices) {
    return new BizTemplate<List<IdKey<Long, Object>>>() {
      Set<Long> policyIds;
      List<AuthPolicy> policiesDb;

      @Override
      protected void checkParams() {
        // Check the department existed
        deptManager.checkAndFind(deptId);

        // Check the policy existed
        policyIds = deptPolices.stream().map(AuthPolicyOrg::getPolicyId)
            .collect(Collectors.toSet());
        policiesDb = authPolicyQuery.checkAndFindTenantPolicy(policyIds, true, true);

        // Check the policy permission
        authPolicyQuery.checkAuthPolicyPermission(policiesDb);
      }

      @Override
      protected List<IdKey<Long, Object>> process() {
        // De-duplication of authorized polices
        List<Long> existedPolicyIds = authPolicyOrgRepo
            .findPolicyIdsByUserIdAndOrgTypeAndPolicyIdIn(deptId, OrgTargetType.DEPT.getValue(),
                policyIds);
        policyIds.removeAll(existedPolicyIds);

        if (isNotEmpty(policyIds)) {
          // Complete authorization information
          assemblePolicyAuthInfo(deptPolices, policiesDb);

          // Save nonexistent authorization
          List<AuthPolicyOrg> addDeptPolices = deptPolices.stream()
              .filter(x -> policyIds.contains(x.getPolicyId())).collect(Collectors.toList());
          if (isNotEmpty(addDeptPolices)) {
            return batchInsert(addDeptPolices);
          }
        }
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void deptPolicyDelete(Long deptId, Set<Long> policyIds) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        // Check the policy permission
        authPolicyQuery.checkAuthPolicyPermission(policyIds);
      }

      @Override
      protected Void process() {
        authPolicyOrgRepo.deleteByOrgIdAndOrgTypeAndPolicyIdIn(deptId,
            AuthOrgType.DEPT.getValue(), policyIds);
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void deptPolicyDeleteBatch(HashSet<Long> deptIds, HashSet<Long> policyIds) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        // NOOP:: Check the policy permission <- UC deletes policies when deleting depts, and does not check permissions.
      }

      @Override
      protected Void process() {
        if (isEmpty(policyIds)) {
          authPolicyOrgRepo.deleteByOrgIdInAndOrgType(deptIds, AuthOrgType.DEPT.getValue());
        } else {
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
