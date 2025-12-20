package cloud.xcan.angus.core.gm.domain.email;

import java.util.List;
import java.util.Optional;

public interface EmailRepo {
    
    Email save(Email email);
    
    Optional<Email> findById(Long id);
    
    List<Email> findByStatus(EmailStatus status);
    
    List<Email> findByType(EmailType type);
    
    List<Email> findByToRecipient(String recipient);
    
    List<Email> findBySubjectContaining(String subject);
    
    List<Email> findAll();
    
    void delete(Long id);
    
    long count();
    
    long countByStatus(EmailStatus status);
    
    long countByType(EmailType type);
}
