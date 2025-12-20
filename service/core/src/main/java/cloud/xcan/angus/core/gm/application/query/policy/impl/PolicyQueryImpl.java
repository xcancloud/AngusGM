package cloud.xcan.angus.core.gm.application.query.policy.impl;

import cloud.xcan.angus.core.gm.application.query.policy.PolicyQuery;
import cloud.xcan.angus.core.gm.domain.policy.Policy;
import cloud.xcan.angus.core.gm.domain.policy.PolicyRepo;
import cloud.xcan.angus.core.gm.domain.policy.enums.PolicyEffect;
import cloud.xcan.angus.core.gm.domain.policy.enums.PolicyStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PolicyQueryImpl implements PolicyQuery {
    private final PolicyRepo policyRepo;

    @Override
    public Optional<Policy> findById(Long id) {
        return policyRepo.findById(id);
    }

    @Override
    public Page<Policy> find(PolicyStatus status, PolicyEffect effect, Integer priority, Pageable pageable) {
        return policyRepo.find(status, effect, priority, pageable);
    }

    @Override
    public long countTotal() {
        return policyRepo.count();
    }

    @Override
    public long countByStatus(PolicyStatus status) {
        return policyRepo.countByStatus(status);
    }

    @Override
    public long countByEffect(PolicyEffect effect) {
        return policyRepo.countByEffect(effect);
    }
}
