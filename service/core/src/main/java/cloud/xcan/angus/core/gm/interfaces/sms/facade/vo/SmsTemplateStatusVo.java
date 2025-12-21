package cloud.xcan.angus.core.gm.interfaces.sms.facade.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SmsTemplateStatusVo {
    private Long id;
    private String status;
    private LocalDateTime modifiedDate;
}
