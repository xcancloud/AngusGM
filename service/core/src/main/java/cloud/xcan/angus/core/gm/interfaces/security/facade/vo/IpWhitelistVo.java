package cloud.xcan.angus.core.gm.interfaces.security.facade.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IpWhitelistVo {
    private Long tenantId;
    private Long createdBy;
    private String creator;
    private LocalDateTime createdDate;
    private Long modifiedBy;
    private String modifier;
    private LocalDateTime modifiedDate;
    
    private Long id;
    private String ipAddress;
    private String ipRange;
    private String description;
    private String status;
    private LocalDateTime lastUsed;
    private Long usageCount;
}
