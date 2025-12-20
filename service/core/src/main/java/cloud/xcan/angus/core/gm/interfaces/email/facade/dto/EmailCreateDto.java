package cloud.xcan.angus.core.gm.interfaces.email.facade.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class EmailCreateDto {
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
    private String type;
    private Integer priority;
}
