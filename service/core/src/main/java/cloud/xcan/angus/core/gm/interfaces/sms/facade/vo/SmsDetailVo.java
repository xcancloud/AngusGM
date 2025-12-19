package cloud.xcan.angus.core.gm.interfaces.sms.facade.vo;

import cloud.xcan.angus.core.gm.domain.sms.SmsStatus;
import cloud.xcan.angus.core.gm.domain.sms.SmsType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SmsDetailVo {
    private Long id;
    private String phone;
    private String content;
    private String templateCode;
    private Object templateParams;
    private SmsStatus status;
    private SmsType type;
    private String provider;
    private LocalDateTime sendTime;
    private LocalDateTime deliverTime;
    private String errorCode;
    private String errorMessage;
    private String externalId;
    private Integer retryCount;
    private Integer maxRetry;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
