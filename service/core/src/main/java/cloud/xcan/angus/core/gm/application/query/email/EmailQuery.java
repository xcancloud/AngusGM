package cloud.xcan.angus.core.gm.application.query.email;

import cloud.xcan.angus.core.gm.domain.email.Email;
import cloud.xcan.angus.core.gm.domain.email.EmailStatus;
import cloud.xcan.angus.core.gm.domain.email.EmailType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface EmailQuery {
    
    Optional<Email> findById(Long id);
    
    List<Email> findAll();
    
    List<Email> findByStatus(EmailStatus status);
    
    List<Email> findByType(EmailType type);
    
    List<Email> findByRecipient(String recipient);
    
    List<Email> findBySubject(String subject);
    
    Map<String, Object> getStatistics();
}
