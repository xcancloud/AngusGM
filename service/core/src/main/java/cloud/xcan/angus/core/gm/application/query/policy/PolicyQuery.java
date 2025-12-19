package cloud.xcan.angus.core.gm.application.query.policy;

import cloud.xcan.angus.core.gm.domain.policy.Policy;
import cloud.xcan.angus.core.gm.domain.policy.enums.PolicyEffect;
import cloud.xcan.angus.core.gm.domain.policy.enums.PolicyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface PolicyQuery {
    Optional<Policy> findById(Long id);
    Page<Policy> find(PolicyStatus status, PolicyEffect effect, Integer priority, Pageable pageable);
    long countTotal();
    long countByStatus(PolicyStatus status);
    long countByEffect(PolicyEffect effect);
}
