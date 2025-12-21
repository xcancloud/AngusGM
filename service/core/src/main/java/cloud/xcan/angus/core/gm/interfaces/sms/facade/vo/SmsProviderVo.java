package cloud.xcan.angus.core.gm.interfaces.sms.facade.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class SmsProviderVo {
    private Long tenantId;
    private Long createdBy;
    private String creator;
    private LocalDateTime createdDate;
    private Long modifiedBy;
    private String modifier;
    private LocalDateTime modifiedDate;
    
    private Long id;
    private String name;
    private String code;
    private Boolean isDefault;
    private Boolean isEnabled;
    private Map<String, String> config;
    private Long balance;
    private Long monthlyQuota;
}
