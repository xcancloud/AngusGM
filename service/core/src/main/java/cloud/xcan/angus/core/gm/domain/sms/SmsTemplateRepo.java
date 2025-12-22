package cloud.xcan.angus.core.gm.domain.sms;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

/**
 * <p>SMS template repository interface</p>
 */
@NoRepositoryBean
public interface SmsTemplateRepo extends BaseRepository<SmsTemplate, Long> {
    
    /**
     * <p>Check if code exists</p>
     */
    boolean existsByCode(String code);
    
    /**
     * <p>Check if code exists excluding specific id</p>
     */
    boolean existsByCodeAndIdNot(String code, Long id);
    
    /**
     * <p>Find by code</p>
     */
    Optional<SmsTemplate> findByCode(String code);
}

