package cloud.xcan.angus.core.gm.infra.persistence.postgres.email;

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
public class EmailRepoPostgres implements EmailRepo {
    
    @Override
    public Email save(Email email) {
        // PostgreSQL implementation
        return email;
    }
    
    @Override
    public Optional<Email> findById(Long id) {
        // PostgreSQL implementation
        return Optional.empty();
    }
    
    @Override
    public List<Email> findByStatus(EmailStatus status) {
        // PostgreSQL implementation
        return List.of();
    }
    
    @Override
    public List<Email> findByType(EmailType type) {
        // PostgreSQL implementation
        return List.of();
    }
    
    @Override
    public List<Email> findByToRecipient(String recipient) {
        // PostgreSQL implementation
        return List.of();
    }
    
    @Override
    public List<Email> findBySubjectContaining(String subject) {
        // PostgreSQL implementation
        return List.of();
    }
    
    @Override
    public List<Email> findAll() {
        // PostgreSQL implementation
        return List.of();
    }
    
    @Override
    public void delete(Long id) {
        // PostgreSQL implementation
    }
    
    @Override
    public long count() {
        // PostgreSQL implementation
        return 0;
    }
    
    @Override
    public long countByStatus(EmailStatus status) {
        // PostgreSQL implementation
        return 0;
    }
    
    @Override
    public long countByType(EmailType type) {
        // PostgreSQL implementation
        return 0;
    }
}
