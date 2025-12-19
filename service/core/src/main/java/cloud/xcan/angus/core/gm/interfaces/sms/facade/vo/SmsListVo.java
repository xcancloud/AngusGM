package cloud.xcan.angus.core.gm.interfaces.sms.facade.vo;

import cloud.xcan.angus.core.gm.domain.sms.SmsStatus;
import cloud.xcan.angus.core.gm.domain.sms.SmsType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SmsListVo {
    private Long id;
    private String phone;
    private String content;
    private SmsStatus status;
    private SmsType type;
    private String provider;
    private LocalDateTime sendTime;
    private Integer retryCount;
    private LocalDateTime createdAt;
}
