package cloud.xcan.angus.core.gm.domain.email;

import cloud.xcan.angus.core.gm.domain.shared.BaseEntity;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class Email extends BaseEntity {
    
    private String subject;
    
    @Type(JsonType.class)
    private List<String> toRecipients;
    
    @Type(JsonType.class)
    private List<String> ccRecipients;
    
    @Type(JsonType.class)
    private List<String> bccRecipients;
    
    private String replyTo;
    
    private String htmlContent;
    
    private String textContent;
    
    private String templateId;
    
    @Type(JsonType.class)
    private Map<String, Object> templateParams;
    
    @Type(JsonType.class)
    private List<Map<String, Object>> attachments;
    
    private EmailStatus status;
    
    private EmailType type;
    
    private Integer priority;
    
    private String provider;
    
    private String externalId;
    
    private LocalDateTime sendTime;
    
    private LocalDateTime deliverTime;
    
    private String errorCode;
    
    private String errorMessage;
    
    private Integer retryCount;
    
    private Integer maxRetry;
}
