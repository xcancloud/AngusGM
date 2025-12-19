package cloud.xcan.angus.core.gm.interfaces.email.facade.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class EmailUpdateDto {
    private String subject;
    private List<String> toRecipients;
    private List<String> ccRecipients;
    private List<String> bccRecipients;
    private String htmlContent;
    private String textContent;
    private List<Map<String, Object>> attachments;
}
