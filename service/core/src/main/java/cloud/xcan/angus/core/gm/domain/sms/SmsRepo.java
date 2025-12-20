package cloud.xcan.angus.core.gm.domain.sms;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * SMS仓储接口
 */
public interface SmsRepo extends JpaRepository<Sms, String>, JpaSpecificationExecutor<Sms> {

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
     * 统计指定类型的SMS数量
     */
    long countByType(SmsType type);
}
