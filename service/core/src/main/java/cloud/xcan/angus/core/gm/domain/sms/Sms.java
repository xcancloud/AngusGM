package cloud.xcan.angus.core.gm.domain.sms;

import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * <p>SMS domain entity</p>
 */
@Getter
@Setter
@Entity
@Table(name = "gm_sms")
public class Sms extends TenantAuditingEntity<Sms, Long> {

    @Id
    private Long id;

    @Column(name = "phone", length = 20, nullable = false)
    private String phone;
    
    @Column(name = "template_id")
    private Long templateId;
    
    @Column(name = "cost", precision = 10, scale = 2)
    private java.math.BigDecimal cost;
    
    @Column(name = "message_id", length = 100)
    private String messageId;

    @Column(name = "content", length = 1000, nullable = false)
    private String content;

    @Column(name = "template_code", length = 50)
    private String templateCode;

    @Type(JsonType.class)
    @Column(name = "template_params", columnDefinition = "jsonb")
    private Object templateParams;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private SmsStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 20, nullable = false)
    private SmsType type;

    @Column(name = "provider", length = 50)
    private String provider;

    @Column(name = "send_time")
    private LocalDateTime sendTime;

    @Column(name = "deliver_time")
    private LocalDateTime deliverTime;

    @Column(name = "error_code", length = 50)
    private String errorCode;

    @Column(name = "error_message", length = 500)
    private String errorMessage;

    @Column(name = "external_id", length = 100)
    private String externalId;

    @Column(name = "retry_count")
    private Integer retryCount;

    @Column(name = "max_retry")
    private Integer maxRetry;
    
    @Override
    public Long identity() {
        return id;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sms sms)) return false;
        return Objects.equals(id, sms.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
