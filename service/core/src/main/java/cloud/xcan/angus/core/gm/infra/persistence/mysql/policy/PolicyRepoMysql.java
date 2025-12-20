package cloud.xcan.angus.core.gm.infra.persistence.mysql.policy;

import cloud.xcan.angus.core.gm.domain.policy.Policy;
import cloud.xcan.angus.core.gm.domain.policy.PolicyRepo;
import cloud.xcan.angus.core.gm.domain.policy.enums.PolicyEffect;
import cloud.xcan.angus.core.gm.domain.policy.enums.PolicyStatus;
import cloud.xcan.angus.infra.jpa.mysql.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface PolicyRepoMysql extends PolicyRepo, BaseRepository<Policy, Long> {
    Optional<Policy> findByName(String name);
    long countByStatus(PolicyStatus status);
    long countByEffect(PolicyEffect effect);
    
    default Page<Policy> find(PolicyStatus status, PolicyEffect effect, Integer priority, Pageable pageable) {
        Specification<Policy> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (status != null) predicates.add(cb.equal(root.get("status"), status));
            if (effect != null) predicates.add(cb.equal(root.get("effect"), effect));
            if (priority != null) predicates.add(cb.equal(root.get("priority"), priority));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return findAll(spec, pageable);
    }
}
