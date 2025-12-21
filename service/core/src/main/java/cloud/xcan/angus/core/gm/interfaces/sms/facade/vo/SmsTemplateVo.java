package cloud.xcan.angus.core.gm.interfaces.sms.facade.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SmsTemplateVo {
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
    private String type;
    private String content;
    private List<String> params;
    private String status;
    private Long usageCount;
    private String provider;
    private String templateCode;
}
