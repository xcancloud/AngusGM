package cloud.xcan.angus.core.gm.infra.persistence.mysql.email;

import cloud.xcan.angus.core.gm.domain.email.Email;
import cloud.xcan.angus.core.gm.domain.email.EmailRepo;
import cloud.xcan.angus.core.gm.domain.email.EmailStatus;
import cloud.xcan.angus.core.gm.domain.email.EmailType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EmailRepoMysql implements EmailRepo {
    
    @Override
    public Email save(Email email) {
        // MySQL implementation
        return email;
    }
    
    @Override
    public Optional<Email> findById(Long id) {
        // MySQL implementation
        return Optional.empty();
    }
    
    @Override
    public List<Email> findByStatus(EmailStatus status) {
        // MySQL implementation
        return List.of();
    }
    
    @Override
    public List<Email> findByType(EmailType type) {
        // MySQL implementation
        return List.of();
    }
    
    @Override
    public List<Email> findByToRecipient(String recipient) {
        // MySQL implementation
        return List.of();
    }
    
    @Override
    public List<Email> findBySubjectContaining(String subject) {
        // MySQL implementation
        return List.of();
    }
    
    @Override
    public List<Email> findAll() {
        // MySQL implementation
        return List.of();
    }
    
    @Override
    public void delete(Long id) {
        // MySQL implementation
    }
    
    @Override
    public long count() {
        // MySQL implementation
        return 0;
    }
    
    @Override
    public long countByStatus(EmailStatus status) {
        // MySQL implementation
        return 0;
    }
    
    @Override
    public long countByType(EmailType type) {
        // MySQL implementation
        return 0;
    }
}
