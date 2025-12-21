package cloud.xcan.angus.core.gm.interfaces.email.facade.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EmailTemplateStatusVo {
    private Long id;
    private String status;
    private LocalDateTime modifiedDate;
}
