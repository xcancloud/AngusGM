package cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@Schema(description = "接口统计详情VO")
public class InterfaceStatsDetailVo implements Serializable {
    
    @Schema(description = "服务名称")
    private String serviceName;
    
    @Schema(description = "接口路径")
    private String path;
    
    @Schema(description = "请求方法")
    private String method;
    
    @Schema(description = "统计周期")
    private Period period;
    
    @Schema(description = "汇总数据")
    private Summary summary;
    
    @Schema(description = "时间线数据")
    private List<TimelineItem> timeline;
    
    @Schema(description = "响应时间分布")
    private Map<String, Long> responseTimeDistribution;
    
    @Schema(description = "状态码分布")
    private Map<String, Long> statusCodeDistribution;
    
    @Data
    @Schema(description = "统计周期")
    public static class Period implements Serializable {
        @Schema(description = "开始日期")
        private String startDate;
        
        @Schema(description = "结束日期")
        private String endDate;
    }
    
    @Data
    @Schema(description = "汇总数据")
    public static class Summary implements Serializable {
        @Schema(description = "总调用次数")
        private Long totalCalls;
        
        @Schema(description = "成功调用次数")
        private Long successCalls;
        
        @Schema(description = "失败调用次数")
        private Long failedCalls;
        
        @Schema(description = "平均响应时间")
        private Integer avgResponseTime;
        
        @Schema(description = "错误率")
        private Double errorRate;
    }
    
    @Data
    @Schema(description = "时间线数据项")
    public static class TimelineItem implements Serializable {
        @Schema(description = "时间")
        private String time;
        
        @Schema(description = "调用次数")
        private Long calls;
        
        @Schema(description = "平均响应时间")
        private Integer avgResponseTime;
        
        @Schema(description = "错误率")
        private Double errorRate;
    }
}
