package cloud.xcan.angus.core.gm.interfaces.quota.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "租户配额VO")
public class TenantQuotaVo implements Serializable {
    
    @Schema(description = "配额ID")
    private String id;
    
    @Schema(description = "租户ID")
    private String tenantId;
    
    @Schema(description = "租户名称")
    private String tenantName;
    
    @Schema(description = "用户配额")
    private ResourceUsage users;
    
    @Schema(description = "存储配额")
    private ResourceUsage storage;
    
    @Schema(description = "应用配额")
    private ResourceUsage applications;
    
    @Schema(description = "API调用配额")
    private ResourceUsage apiCalls;
    
    @Schema(description = "状态")
    private String status;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdDate;
    
    @Schema(description = "修改时间")
    private LocalDateTime modifiedDate;
    
    @Data
    @Schema(description = "资源使用情况")
    public static class ResourceUsage implements Serializable {
        @Schema(description = "配额")
        private Object quota;
        
        @Schema(description = "已使用")
        private Object used;
        
        @Schema(description = "使用百分比")
        private Double usagePercent;
        
        @Schema(description = "可用")
        private Object available;
    }
}
