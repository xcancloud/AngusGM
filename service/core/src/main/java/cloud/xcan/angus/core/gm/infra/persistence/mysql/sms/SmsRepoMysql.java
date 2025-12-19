package cloud.xcan.angus.core.gm.infra.persistence.mysql.sms;

import cloud.xcan.angus.core.gm.domain.sms.Sms;
import cloud.xcan.angus.core.gm.domain.sms.SmsRepo;
import cloud.xcan.angus.core.gm.domain.sms.SmsStatus;
import cloud.xcan.angus.core.gm.domain.sms.SmsType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Profile("mysql")
@Repository
@RequiredArgsConstructor
public class SmsRepoMysql implements SmsRepo {

    private final SmsJpaRepo jpaRepo;

    @Override
    public Sms save(Sms sms) {
        return jpaRepo.save(sms);
    }

    @Override
    public Optional<Sms> findById(Long id) {
        return jpaRepo.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepo.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepo.existsById(id);
    }

    @Override
    public Page<Sms> findAll(SmsStatus status, SmsType type, String phone, String templateCode, Pageable pageable) {
        return jpaRepo.findAll((Specification<Sms>) (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type));
            }
            if (phone != null && !phone.isEmpty()) {
                predicates.add(cb.like(root.get("phone"), "%" + phone + "%"));
            }
            if (templateCode != null && !templateCode.isEmpty()) {
                predicates.add(cb.equal(root.get("templateCode"), templateCode));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }

    @Override
    public long count() {
        return jpaRepo.count();
    }

    @Override
    public long countByStatus(SmsStatus status) {
        return jpaRepo.countByStatus(status);
    }

    @Override
    public long countByType(SmsType type) {
        return jpaRepo.countByType(type);
    }

    interface SmsJpaRepo extends JpaRepository<Sms, Long>, JpaSpecificationExecutor<Sms> {
        long countByStatus(SmsStatus status);
        long countByType(SmsType type);
    }
}
