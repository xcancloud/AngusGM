package cloud.xcan.angus.core.gm.interfaces.quota.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@Schema(description = "租户配额详情VO")
public class TenantQuotaDetailVo implements Serializable {
    
    @Schema(description = "租户ID")
    private String tenantId;
    
    @Schema(description = "租户名称")
    private String tenantName;
    
    @Schema(description = "用户配额详情")
    private UserQuotaDetail users;
    
    @Schema(description = "存储配额详情")
    private StorageQuotaDetail storage;
    
    @Schema(description = "应用配额详情")
    private ApplicationQuotaDetail applications;
    
    @Schema(description = "API调用配额详情")
    private ApiCallsQuotaDetail apiCalls;
    
    @Schema(description = "状态")
    private String status;
    
    @Data
    @Schema(description = "用户配额详情")
    public static class UserQuotaDetail implements Serializable {
        @Schema(description = "配额")
        private Integer quota;
        
        @Schema(description = "已使用")
        private Integer used;
        
        @Schema(description = "使用百分比")
        private Double usagePercent;
        
        @Schema(description = "可用")
        private Integer available;
        
        @Schema(description = "趋势数据")
        private List<TrendItem> trend;
    }
    
    @Data
    @Schema(description = "存储配额详情")
    public static class StorageQuotaDetail implements Serializable {
        @Schema(description = "配额")
        private String quota;
        
        @Schema(description = "已使用")
        private String used;
        
        @Schema(description = "使用百分比")
        private Double usagePercent;
        
        @Schema(description = "可用")
        private String available;
        
        @Schema(description = "存储分解")
        private Map<String, String> breakdown;
        
        @Schema(description = "趋势数据")
        private List<StorageTrendItem> trend;
    }
    
    @Data
    @Schema(description = "应用配额详情")
    public static class ApplicationQuotaDetail implements Serializable {
        @Schema(description = "配额")
        private Integer quota;
        
        @Schema(description = "已使用")
        private Integer used;
        
        @Schema(description = "使用百分比")
        private Double usagePercent;
        
        @Schema(description = "可用")
        private Integer available;
        
        @Schema(description = "应用列表")
        private List<AppInfo> list;
    }
    
    @Data
    @Schema(description = "API调用配额详情")
    public static class ApiCallsQuotaDetail implements Serializable {
        @Schema(description = "配额")
        private Long quota;
        
        @Schema(description = "已使用")
        private Long used;
        
        @Schema(description = "使用百分比")
        private Double usagePercent;
        
        @Schema(description = "可用")
        private Long available;
        
        @Schema(description = "日均调用量")
        private Integer dailyAverage;
        
        @Schema(description = "趋势数据")
        private List<TrendItem> trend;
    }
    
    @Data
    @Schema(description = "趋势数据项")
    public static class TrendItem implements Serializable {
        @Schema(description = "日期")
        private String date;
        
        @Schema(description = "数量")
        private Long count;
    }
    
    @Data
    @Schema(description = "存储趋势数据项")
    public static class StorageTrendItem implements Serializable {
        @Schema(description = "日期")
        private String date;
        
        @Schema(description = "大小")
        private String size;
    }
    
    @Data
    @Schema(description = "应用信息")
    public static class AppInfo implements Serializable {
        @Schema(description = "应用名称")
        private String name;
        
        @Schema(description = "是否启用")
        private Boolean enabled;
    }
}
