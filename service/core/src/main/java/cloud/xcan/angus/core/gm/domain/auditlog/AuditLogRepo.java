package cloud.xcan.angus.core.gm.domain.auditlog;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * Audit log repository interface
 * </p>
 */
public interface AuditLogRepo extends JpaRepository<AuditLog, Long>, JpaSpecificationExecutor<AuditLog> {

    /**
     * Delete logs by level and created date before
     */
    void deleteByLevelAndCreatedDateBefore(String level, LocalDateTime beforeDate);

    /**
     * Delete logs by created date before
     */
    void deleteByCreatedDateBefore(LocalDateTime beforeDate);

    /**
     * Count logs by date range
     */
    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.operationTime >= :startDate AND a.operationTime <= :endDate")
    long countByOperationTimeBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Count logs by level and date range
     */
    @Query("SELECT a.level, COUNT(a) FROM AuditLog a WHERE a.operationTime >= :startDate AND a.operationTime <= :endDate GROUP BY a.level")
    List<Object[]> countByLevelGroupBy(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Count logs by module and date range
     */
    @Query("SELECT a.module, COUNT(a) FROM AuditLog a WHERE a.operationTime >= :startDate AND a.operationTime <= :endDate GROUP BY a.module")
    List<Object[]> countByModuleGroupBy(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Count logs by user and date range, return top N users
     */
    @Query("SELECT a.userId, a.username, COUNT(a) as cnt FROM AuditLog a WHERE a.operationTime >= :startDate AND a.operationTime <= :endDate AND a.userId IS NOT NULL GROUP BY a.userId, a.username ORDER BY cnt DESC")
    List<Object[]> countByUserGroupBy(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    /**
     * Count logs by module and operation type
     */
    @Query("SELECT a.module, a.operation, COUNT(a) FROM AuditLog a WHERE a.operationTime >= :startDate AND a.operationTime <= :endDate GROUP BY a.module, a.operation")
    List<Object[]> countByModuleAndOperationGroupBy(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
