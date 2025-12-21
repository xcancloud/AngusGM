package cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "实时响应时间VO")
public class RealtimeResponseTimeVo implements Serializable {
    
    @Schema(description = "当前平均响应时间（毫秒）")
    private Integer current;
    
    @Schema(description = "P50响应时间")
    private Integer p50;
    
    @Schema(description = "P95响应时间")
    private Integer p95;
    
    @Schema(description = "P99响应时间")
    private Integer p99;
    
    @Schema(description = "最大响应时间")
    private Integer max;
    
    @Schema(description = "时间线数据")
    private List<TimelineItem> timeline;
    
    @Data
    @Schema(description = "时间线数据项")
    public static class TimelineItem implements Serializable {
        @Schema(description = "时间")
        private String time;
        
        @Schema(description = "平均响应时间")
        private Integer avg;
        
        @Schema(description = "P95响应时间")
        private Integer p95;
    }
}
