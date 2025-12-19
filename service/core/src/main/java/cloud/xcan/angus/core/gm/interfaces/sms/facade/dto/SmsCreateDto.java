package cloud.xcan.angus.core.gm.interfaces.sms.facade.dto;

import cloud.xcan.angus.core.gm.domain.sms.SmsType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SmsCreateDto {
    @NotBlank
    private String phone;
    
    @NotBlank
    private String content;
    
    private String templateCode;
    
    private Object templateParams;
    
    @NotNull
    private SmsType type;
    
    private String provider;
    
    private Integer maxRetry;
}
