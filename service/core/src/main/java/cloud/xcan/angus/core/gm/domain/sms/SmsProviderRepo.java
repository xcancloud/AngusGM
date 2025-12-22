package cloud.xcan.angus.core.gm.domain.sms;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

/**
 * <p>SMS provider repository interface</p>
 */
@NoRepositoryBean
public interface SmsProviderRepo extends BaseRepository<SmsProvider, Long> {
    
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
    Optional<SmsProvider> findByCode(String code);
    
    /**
     * <p>Find default provider</p>
     */
    Optional<SmsProvider> findByIsDefaultTrue();
    
    /**
     * <p>Find all enabled providers</p>
     */
    List<SmsProvider> findByIsEnabledTrue();
}

