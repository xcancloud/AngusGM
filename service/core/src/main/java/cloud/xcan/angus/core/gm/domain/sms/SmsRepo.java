package cloud.xcan.angus.core.gm.domain.sms;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.NoRepositoryBean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * <p>SMS repository interface</p>
 */
@NoRepositoryBean
public interface SmsRepo extends BaseRepository<Sms, Long> {

    /**
     * 根据状态查询SMS列表
     */
    List<Sms> findByStatus(SmsStatus status);

    /**
     * 根据类型查询SMS列表
     */
    List<Sms> findByType(SmsType type);

    /**
     * 根据手机号查询SMS列表
     */
    List<Sms> findByPhone(String phone);

    /**
     * 根据外部ID查询
     */
    Optional<Sms> findByExternalId(String externalId);

    /**
     * 根据状态和发送时间范围查询
     */
    List<Sms> findByStatusAndSendTimeBetween(SmsStatus status, LocalDateTime start, LocalDateTime end);

    /**
     * 统计指定状态的SMS数量
     */
    long countByStatus(SmsStatus status);

    /**
     * <p>Count by type</p>
     */
    long countByType(SmsType type);
    
    /**
     * <p>Find by template ID</p>
     */
    List<Sms> findByTemplateId(Long templateId);
    
    /**
     * <p>Count by template ID</p>
     */
    long countByTemplateId(Long templateId);
    
    /**
     * <p>Find all with specification</p>
     */
    Page<Sms> findAll(Specification<Sms> spec, Pageable pageable);
}
