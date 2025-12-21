package cloud.xcan.angus.core.gm.interfaces.auditlog.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@Schema(description = "审计日志统计VO")
public class AuditLogStatsVo implements Serializable {
    
    @Schema(description = "统计周期")
    private Period period;
    
    @Schema(description = "总日志数")
    private Long totalLogs;
    
    @Schema(description = "按级别统计")
    private Map<String, Long> byLevel;
    
    @Schema(description = "按模块统计")
    private Map<String, Long> byModule;
    
    @Schema(description = "TOP操作用户")
    private List<TopUser> topUsers;
    
    @Data
    @Schema(description = "统计周期")
    public static class Period implements Serializable {
        @Schema(description = "开始日期")
        private String startDate;
        
        @Schema(description = "结束日期")
        private String endDate;
    }
    
    @Data
    @Schema(description = "TOP操作用户")
    public static class TopUser implements Serializable {
        @Schema(description = "用户ID")
        private String userId;
        
        @Schema(description = "用户名")
        private String userName;
        
        @Schema(description = "操作次数")
        private Long operationCount;
    }
}
