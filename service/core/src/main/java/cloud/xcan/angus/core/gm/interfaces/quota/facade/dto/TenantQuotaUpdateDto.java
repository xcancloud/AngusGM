package cloud.xcan.angus.core.gm.interfaces.quota.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "租户配额更新DTO")
public class TenantQuotaUpdateDto implements Serializable {
    
    @Schema(description = "用户配额")
    private ResourceQuota users;
    
    @Schema(description = "存储配额")
    private ResourceQuota storage;
    
    @Schema(description = "应用配额")
    private ResourceQuota applications;
    
    @Schema(description = "API调用配额")
    private ResourceQuota apiCalls;
    
    @Data
    @Schema(description = "资源配额")
    public static class ResourceQuota implements Serializable {
        @Schema(description = "配额值")
        private Object quota;
    }
}
