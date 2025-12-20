package cloud.xcan.angus.core.gm.domain.apimonitoring;

import cloud.xcan.angus.core.gm.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * API监控实体
 * API接口监控管理
 */
@Getter
@Setter
@Entity
@Table(name = "gm_api_monitoring")
public class ApiMonitoring extends BaseEntity {

    @Column(name = "endpoint", length = 500, nullable = false)
    private String endpoint;

    @Column(name = "method", length = 10, nullable = false)
    private String method;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 50, nullable = false)
    private ApiMonitoringType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private ApiMonitoringStatus status;

    @Column(name = "status_code")
    private Integer statusCode;

    @Column(name = "response_time")
    private Long responseTime;

    @Column(name = "request_payload", columnDefinition = "text")
    private String requestPayload;

    @Column(name = "response_payload", columnDefinition = "text")
    private String responsePayload;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    @Column(name = "request_time")
    private LocalDateTime requestTime;
}
