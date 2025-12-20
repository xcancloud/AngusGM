package cloud.xcan.angus.core.gm.interfaces.email.facade.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class EmailDetailVo {
    private Long id;
    private String subject;
    private List<String> toRecipients;
    private List<String> ccRecipients;
    private List<String> bccRecipients;
    private String replyTo;
    private String htmlContent;
    private String textContent;
    private String templateId;
    private Map<String, Object> templateParams;
    private List<Map<String, Object>> attachments;
    private String status;
    private String type;
    private Integer priority;
    private String provider;
    private String externalId;
    private LocalDateTime sendTime;
    private LocalDateTime deliverTime;
    private String errorCode;
    private String errorMessage;
    private Integer retryCount;
    private Integer maxRetry;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
