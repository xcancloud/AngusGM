package cloud.xcan.angus.core.gm.application.query.email.impl;

import cloud.xcan.angus.core.gm.application.query.email.EmailQuery;
import cloud.xcan.angus.core.gm.domain.email.Email;
import cloud.xcan.angus.core.gm.domain.email.EmailRepo;
import cloud.xcan.angus.core.gm.domain.email.EmailStatus;
import cloud.xcan.angus.core.gm.domain.email.EmailType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class EmailQueryImpl implements EmailQuery {
    
    private final EmailRepo emailRepo;
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Email> findById(Long id) {
        return emailRepo.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Email> findAll() {
        return emailRepo.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Email> findByStatus(EmailStatus status) {
        return emailRepo.findByStatus(status);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Email> findByType(EmailType type) {
        return emailRepo.findByType(type);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Email> findByRecipient(String recipient) {
        return emailRepo.findByToRecipient(recipient);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Email> findBySubject(String subject) {
        return emailRepo.findBySubjectContaining(subject);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        long total = emailRepo.count();
        stats.put("total", total);
        
        Map<String, Long> statusCounts = new HashMap<>();
        for (EmailStatus status : EmailStatus.values()) {
            statusCounts.put(status.name(), emailRepo.countByStatus(status));
        }
        stats.put("byStatus", statusCounts);
        
        Map<String, Long> typeCounts = new HashMap<>();
        for (EmailType type : EmailType.values()) {
            typeCounts.put(type.name(), emailRepo.countByType(type));
        }
        stats.put("byType", typeCounts);
        
        return stats;
    }
}
