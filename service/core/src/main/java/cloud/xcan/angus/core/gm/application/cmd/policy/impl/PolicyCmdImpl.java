package cloud.xcan.angus.core.gm.application.cmd.policy.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.policy.PolicyCmd;
import cloud.xcan.angus.core.gm.application.query.policy.PolicyQuery;
import cloud.xcan.angus.core.gm.domain.authorization.AuthorizationRepo;
import cloud.xcan.angus.core.gm.domain.policy.Policy;
import cloud.xcan.angus.core.gm.domain.policy.PolicyRepo;
import cloud.xcan.angus.core.gm.domain.policy.enums.PolicyStatus;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.remote.message.http.ResourceExisted;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of policy command service
 */
@Biz
public class PolicyCmdImpl extends CommCmd<Policy, Long> implements PolicyCmd {

  @Resource
  private PolicyRepo policyRepo;

  @Resource
  private PolicyQuery policyQuery;

  @Resource
  private AuthorizationRepo authorizationRepo;

  @Resource
  private ObjectMapper objectMapper;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Policy create(Policy policy) {
    return new BizTemplate<Policy>() {
      @Override
      protected void checkParams() {
        if (policyRepo.existsByName(policy.getName())) {
          throw ResourceExisted.of("角色名称「{0}」已存在", new Object[]{policy.getName()});
        }
        if (policy.getCode() != null && policyRepo.existsByCode(policy.getCode())) {
          throw ResourceExisted.of("角色编码「{0}」已存在", new Object[]{policy.getCode()});
        }
        if (Boolean.TRUE.equals(policy.getIsDefault()) && policy.getAppId() != null) {
          Policy existingDefault = policyRepo.findByAppIdAndIsDefaultTrue(policy.getAppId());
          if (existingDefault != null) {
            throw ResourceExisted.of("应用「{0}」已存在默认角色", new Object[]{policy.getAppId()});
          }
        }
      }

      @Override
      protected Policy process() {
        if (policy.getStatus() == null) {
          policy.setStatus(PolicyStatus.ENABLED);
        }
        if (policy.getIsSystem() == null) {
          policy.setIsSystem(false);
        }
        if (policy.getIsDefault() == null) {
          policy.setIsDefault(false);
        }
        // Serialize permissions to JSON
        if (policy.getPermissionList() != null) {
          try {
            policy.setPermissions(objectMapper.writeValueAsString(policy.getPermissionList()));
          } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize permissions", e);
          }
        }
        insert(policy);
        return policy;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Policy update(Policy policy) {
    return new BizTemplate<Policy>() {
      Policy policyDb;

      @Override
      protected void checkParams() {
        policyDb = policyQuery.findAndCheck(policy.getId());

        if (policy.getName() != null && !policy.getName().equals(policyDb.getName())) {
          if (policyRepo.existsByNameAndIdNot(policy.getName(), policy.getId())) {
            throw ResourceExisted.of("角色名称「{0}」已存在", new Object[]{policy.getName()});
          }
        }
        if (Boolean.TRUE.equals(policy.getIsDefault()) && policy.getAppId() != null) {
          Policy existingDefault = policyRepo.findByAppIdAndIsDefaultTrue(policy.getAppId());
          if (existingDefault != null && !existingDefault.getId().equals(policy.getId())) {
            throw ResourceExisted.of("应用「{0}」已存在默认角色", new Object[]{policy.getAppId()});
          }
        }
      }

      @Override
      protected Policy process() {
        // Serialize permissions to JSON if provided
        if (policy.getPermissionList() != null) {
          try {
            policyDb.setPermissions(objectMapper.writeValueAsString(policy.getPermissionList()));
          } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize permissions", e);
          }
        }
        update(policy, policyDb);
        return policyDb;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(Long id) {
    new BizTemplate<Void>() {
      Policy policyDb;

      @Override
      protected void checkParams() {
        policyDb = policyQuery.findAndCheck(id);
        if (Boolean.TRUE.equals(policyDb.getIsSystem())) {
          throw ResourceNotFound.of("系统角色不能删除", new Object[]{});
        }
        if (Boolean.TRUE.equals(policyDb.getIsDefault())) {
          throw ResourceNotFound.of("默认角色不能删除", new Object[]{});
        }
        long userCount = policyRepo.countUsersByPolicyId(id);
        if (userCount > 0) {
          throw ResourceExisted.of("角色下存在用户，无法删除", new Object[]{});
        }
      }

      @Override
      protected Void process() {
        policyRepo.deleteById(id);
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Policy updatePermissions(Long id, List<Policy.PermissionInfo> permissions) {
    return new BizTemplate<Policy>() {
      Policy policyDb;

      @Override
      protected void checkParams() {
        policyDb = policyQuery.findAndCheck(id);
      }

      @Override
      protected Policy process() {
        try {
          policyDb.setPermissions(objectMapper.writeValueAsString(permissions));
          policyDb.setPermissionList(permissions);
          policyRepo.save(policyDb);
          return policyDb;
        } catch (JsonProcessingException e) {
          throw new RuntimeException("Failed to serialize permissions", e);
        }
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Policy setDefault(Long id, Boolean isDefault) {
    return new BizTemplate<Policy>() {
      Policy policyDb;

      @Override
      protected void checkParams() {
        policyDb = policyQuery.findAndCheck(id);
        if (Boolean.TRUE.equals(isDefault) && policyDb.getAppId() != null) {
          Policy existingDefault = policyRepo.findByAppIdAndIsDefaultTrue(policyDb.getAppId());
          if (existingDefault != null && !existingDefault.getId().equals(id)) {
            throw ResourceExisted.of("应用「{0}」已存在默认角色", new Object[]{policyDb.getAppId()});
          }
        }
      }

      @Override
      protected Policy process() {
        policyDb.setIsDefault(isDefault);
        policyRepo.save(policyDb);
        return policyDb;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<Policy, Long> getRepository() {
    return policyRepo;
  }
}
