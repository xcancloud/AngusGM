package cloud.xcan.angus.core.gm.application.cmd.policy.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.policy.PolicyCmd;
import cloud.xcan.angus.core.gm.application.query.policy.PolicyQuery;
import cloud.xcan.angus.core.gm.domain.policy.Policy;
import cloud.xcan.angus.core.gm.domain.policy.PolicyRepo;
import cloud.xcan.angus.core.gm.domain.policy.enums.PolicyStatus;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.remote.message.http.ResourceExisted;
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

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Policy create(Policy policy) {
    return new BizTemplate<Policy>() {
      @Override
      protected void checkParams() {
        if (policyRepo.existsByName(policy.getName())) {
          throw ResourceExisted.of("策略名称「{0}」已存在", new Object[]{policy.getName()});
        }
      }

      @Override
      protected Policy process() {
        if (policy.getStatus() == null) {
          policy.setStatus(PolicyStatus.ENABLED);
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
            throw ResourceExisted.of("策略名称「{0}」已存在", new Object[]{policy.getName()});
          }
        }
      }

      @Override
      protected Policy process() {
        update(policy, policyDb);
        return policyDb;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(Long id) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        policyQuery.findAndCheck(id);
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
  public void enable(Long id) {
    new BizTemplate<Void>() {
      Policy policyDb;

      @Override
      protected void checkParams() {
        policyDb = policyQuery.findAndCheck(id);
      }

      @Override
      protected Void process() {
        policyDb.setStatus(PolicyStatus.ENABLED);
        policyRepo.save(policyDb);
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void disable(Long id) {
    new BizTemplate<Void>() {
      Policy policyDb;

      @Override
      protected void checkParams() {
        policyDb = policyQuery.findAndCheck(id);
      }

      @Override
      protected Void process() {
        policyDb.setStatus(PolicyStatus.DISABLED);
        policyRepo.save(policyDb);
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Policy updatePermissions(Long id, List<String> permissions) {
    return new BizTemplate<Policy>() {
      Policy policyDb;

      @Override
      protected void checkParams() {
        policyDb = policyQuery.findAndCheck(id);
      }

      @Override
      protected Policy process() {
        policyDb.setResourceIds(permissions);
        policyRepo.save(policyDb);
        return policyDb;
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
