package cloud.xcan.angus.core.gm.interfaces.email.facade.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EmailListVo {
    private Long id;
    private String subject;
    private List<String> toRecipients;
    private String status;
    private String type;
    private Integer priority;
    private LocalDateTime sendTime;
    private LocalDateTime createdAt;
}
