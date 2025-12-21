package cloud.xcan.angus.core.gm.interfaces.quota.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "资源配额概览VO")
public class QuotaOverviewVo implements Serializable {
    
    @Schema(description = "总租户数")
    private Integer totalTenants;
    
    @Schema(description = "超配额租户数")
    private Integer tenantsOverQuota;
    
    @Schema(description = "接近配额租户数")
    private Integer tenantsNearQuota;
    
    @Schema(description = "总用户配额")
    private Integer totalUsersQuota;
    
    @Schema(description = "已使用用户数")
    private Integer totalUsersUsed;
    
    @Schema(description = "总存储配额")
    private String totalStorageQuota;
    
    @Schema(description = "已使用存储")
    private String totalStorageUsed;
}
