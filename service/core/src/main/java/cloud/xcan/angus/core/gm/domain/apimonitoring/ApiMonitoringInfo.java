package cloud.xcan.angus.core.gm.domain.apimonitoring;

import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * API监控信息实体（用于查询，不包含大文本字段）
 * API接口调用监控、性能分析、异常追踪
 * </p>
 */
@Getter
@Setter
@Entity
@Table(name = "gm_api_monitoring")
public class ApiMonitoringInfo extends TenantAuditingEntity<ApiMonitoringInfo, Long> {

    @Id
    private Long id;

    @Column(name = "trace_id", length = 100)
    private String traceId;

    @Column(name = "service_name", length = 100, nullable = false)
    private String serviceName;

    @Column(name = "path", length = 500, nullable = false)
    private String path;

    @Column(name = "method", length = 10, nullable = false)
    private String method;

    @Column(name = "request_time", nullable = false)
    private LocalDateTime requestTime;

    @Column(name = "duration")
    private Long duration;

    @Column(name = "status_code")
    private Integer statusCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 50)
    private ApiMonitoringType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private ApiMonitoringStatus status;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "user_id", length = 50)
    private String userId;

    @Column(name = "user_name", length = 100)
    private String userName;

    @Type(JsonType.class)
    @Column(name = "query_parameters", columnDefinition = "jsonb")
    private Map<String, String> queryParameters;

    @Type(JsonType.class)
    @Column(name = "request_headers", columnDefinition = "jsonb")
    private Map<String, String> requestHeaders;

    @Type(JsonType.class)
    @Column(name = "response_headers", columnDefinition = "jsonb")
    private Map<String, String> responseHeaders;

    // 忽略大文本字段：requestBody, responseBody, stackTrace

    @Column(name = "error_message", length = 2000)
    private String errorMessage;

    @Column(name = "error_type", length = 200)
    private String errorType;

    // 忽略大文本字段：stackTrace

    @Column(name = "resolved")
    private Boolean resolved;

    @Type(JsonType.class)
    @Column(name = "breakdown", columnDefinition = "jsonb")
    private Map<String, Integer> breakdown;

    @Type(JsonType.class)
    @Column(name = "sql_statements", columnDefinition = "jsonb")
    private List<Map<String, Object>> sqlStatements;

    @Override
    public Long identity() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ApiMonitoringInfo that)) {
            return false;
        }
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

