package cloud.xcan.angus.core.gm.interfaces.sms.facade.dto;

import cloud.xcan.angus.core.gm.domain.sms.SmsStatus;
import cloud.xcan.angus.core.gm.domain.sms.SmsType;
import lombok.Data;

@Data
public class SmsFindDto {
    private SmsStatus status;
    private SmsType type;
    private String phone;
    private String templateCode;
}
