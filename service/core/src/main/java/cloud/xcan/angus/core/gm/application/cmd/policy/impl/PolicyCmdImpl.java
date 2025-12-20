package cloud.xcan.angus.core.gm.application.cmd.policy.impl;

import cloud.xcan.angus.common.exception.ResourceExisted;
import cloud.xcan.angus.common.exception.ResourceNotFound;
import cloud.xcan.angus.common.template.BizTemplate;
import cloud.xcan.angus.core.gm.application.cmd.policy.PolicyCmd;
import cloud.xcan.angus.core.gm.domain.policy.Policy;
import cloud.xcan.angus.core.gm.domain.policy.PolicyRepo;
import cloud.xcan.angus.core.gm.domain.policy.enums.PolicyStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PolicyCmdImpl implements PolicyCmd {

    private final PolicyRepo policyRepo;
    private final BizTemplate bizTemplate;

    @Override
    @Transactional
    public Policy create(Policy policy) {
        return bizTemplate.execute(() -> {
            if (policyRepo.findByName(policy.getName()).isPresent()) {
                throw new ResourceExisted("Policy with name already exists");
            }
            if (policy.getStatus() == null) {
                policy.setStatus(PolicyStatus.ENABLED);
            }
            return policyRepo.save(policy);
        });
    }

    @Override
    @Transactional
    public Policy update(Policy policy) {
        return bizTemplate.execute(() -> {
            Policy existing = policyRepo.findById(policy.getId())
                    .orElseThrow(() -> new ResourceNotFound("Policy not found"));
            if (!existing.getName().equals(policy.getName())) {
                if (policyRepo.findByName(policy.getName()).isPresent()) {
                    throw new ResourceExisted("Policy with name already exists");
                }
            }
            existing.setName(policy.getName());
            existing.setEffect(policy.getEffect());
            existing.setResourceIds(policy.getResourceIds());
            existing.setPriority(policy.getPriority());
            existing.setDescription(policy.getDescription());
            return policyRepo.save(existing);
        });
    }

    @Override
    @Transactional
    public void enable(Long id) {
        bizTemplate.execute(() -> {
            Policy policy = policyRepo.findById(id)
                    .orElseThrow(() -> new ResourceNotFound("Policy not found"));
            policy.setStatus(PolicyStatus.ENABLED);
            policyRepo.save(policy);
            return null;
        });
    }

    @Override
    @Transactional
    public void disable(Long id) {
        bizTemplate.execute(() -> {
            Policy policy = policyRepo.findById(id)
                    .orElseThrow(() -> new ResourceNotFound("Policy not found"));
            policy.setStatus(PolicyStatus.DISABLED);
            policyRepo.save(policy);
            return null;
        });
    }

    @Override
    @Transactional
    public void delete(Long id) {
        bizTemplate.execute(() -> {
            Policy policy = policyRepo.findById(id)
                    .orElseThrow(() -> new ResourceNotFound("Policy not found"));
            policyRepo.delete(policy);
            return null;
        });
    }
}
