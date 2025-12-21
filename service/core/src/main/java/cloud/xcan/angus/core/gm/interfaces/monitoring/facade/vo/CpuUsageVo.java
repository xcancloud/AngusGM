package cloud.xcan.angus.core.gm.interfaces.monitoring.facade.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CpuUsageVo {
    private Double current;
    private Double average;
    private Double max;
    private Double min;
    private Integer cores;
    private List<CpuHistory> history;
    
    @Data
    public static class CpuHistory {
        private LocalDateTime time;
        private Double usage;
    }
}
