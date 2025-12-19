package cloud.xcan.angus.core.gm.interfaces.sms.facade.dto;

import cloud.xcan.angus.core.gm.domain.sms.SmsType;
import lombok.Data;

@Data
public class SmsUpdateDto {
    private Long id;
    
    private String phone;
    
    private String content;
    
    private String templateCode;
    
    private Object templateParams;
    
    private SmsType type;
    
    private String provider;
}
