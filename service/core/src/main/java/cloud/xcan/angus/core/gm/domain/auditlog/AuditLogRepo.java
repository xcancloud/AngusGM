package cloud.xcan.angus.core.gm.domain.auditlog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 审计日志仓储接口
 */
public interface AuditLogRepo extends JpaRepository<AuditLog, Long> {

    /**
     * 根据类型查找
     */
    List<AuditLog> findByType(AuditLogType type);

    /**
     * 根据操作查找
     */
    List<AuditLog> findByAction(AuditLogAction action);

    /**
     * 根据用户ID查找
     */
    Page<AuditLog> findByUserId(Long userId, Pageable pageable);

    /**
     * 根据资源类型查找
     */
    List<AuditLog> findByResourceType(String resourceType);

    /**
     * 根据资源ID查找
     */
    List<AuditLog> findByResourceId(String resourceId);

    /**
     * 根据时间范围查找
     */
    Page<AuditLog> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

    /**
     * 根据用户和时间范围查找
     */
    List<AuditLog> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime startTime, LocalDateTime endTime);
}
