package cloud.xcan.angus.core.gm.domain.sms;

import cloud.xcan.angus.core.gm.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * SMS实体
 * 短信消息管理
 */
@Getter
@Setter
@Entity
@Table(name = "gm_sms")
public class Sms extends BaseEntity {

    @Column(name = "phone", length = 20, nullable = false)
    private String phone;

    @Column(name = "content", length = 1000, nullable = false)
    private String content;

    @Column(name = "template_code", length = 50)
    private String templateCode;

    @Column(name = "template_params", columnDefinition = "jsonb")
    @org.hibernate.annotations.Type(org.hibernate.annotations.type.JsonType.class)
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
}
