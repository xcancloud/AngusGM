package cloud.xcan.angus.core.gm.interfaces.apimonitoring.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "实时QPS VO")
public class RealtimeQpsVo implements Serializable {
    
    @Schema(description = "当前QPS")
    private Integer current;
    
    @Schema(description = "峰值QPS")
    private Integer peak;
    
    @Schema(description = "平均QPS")
    private Integer average;
    
    @Schema(description = "时间线数据")
    private List<TimelineItem> timeline;
    
    @Data
    @Schema(description = "时间线数据项")
    public static class TimelineItem implements Serializable {
        @Schema(description = "时间")
        private String time;
        
        @Schema(description = "QPS")
        private Integer qps;
    }
}
