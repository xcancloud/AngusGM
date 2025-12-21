package cloud.xcan.angus.core.gm.interfaces.sms.facade.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SmsTestVo {
    private String phone;
    private String status;
    private String messageId;
    private LocalDateTime sentTime;
}
