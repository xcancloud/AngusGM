package cloud.xcan.angus.core.gm.domain.auditlog;

import cloud.xcan.angus.core.gm.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 审计日志实体
 * 系统审计日志管理
 */
@Getter
@Setter
@Entity
@Table(name = "gm_audit_log")
public class AuditLog extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 50, nullable = false)
    private AuditLogType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", length = 20, nullable = false)
    private AuditLogAction action;

    @Column(name = "resource_type", length = 100, nullable = false)
    private String resourceType;

    @Column(name = "resource_id", length = 100)
    private String resourceId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", length = 100)
    private String username;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "before_data", columnDefinition = "jsonb")
    @org.hibernate.annotations.Type(org.hibernate.annotations.type.JsonType.class)
    private Object beforeData;

    @Column(name = "after_data", columnDefinition = "jsonb")
    @org.hibernate.annotations.Type(org.hibernate.annotations.type.JsonType.class)
    private Object afterData;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "success", nullable = false)
    private Boolean success = true;

    @Column(name = "error_message", length = 1000)
    private String errorMessage;
}
