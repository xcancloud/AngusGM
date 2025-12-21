package cloud.xcan.angus.core.gm.domain.auditlog;

import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

/**
 * <p>
 * Audit log domain entity
 * </p>
 */
@Getter
@Setter
@Entity
@Table(name = "gm_audit_log")
public class AuditLog extends TenantAuditingEntity<AuditLog, Long> {

    @Id
    private Long id;

    @Column(name = "module", length = 50)
    private String module;

    @Column(name = "operation", length = 50)
    private String operation;

    @Column(name = "level", length = 20)
    private String level;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", length = 100)
    private String username;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "location", length = 100)
    private String location;

    @Column(name = "device", length = 200)
    private String device;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "operation_time")
    private LocalDateTime operationTime;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "success", nullable = false)
    private Boolean success = true;

    @Type(JsonType.class)
    @Column(name = "changes", columnDefinition = "jsonb")
    private Map<String, Object> changes;

    @Override
    public Long identity() {
        return id;
    }
}
