package cloud.xcan.angus.core.gm.interfaces.monitoring.facade.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DatabasePerformanceVo {
    private QueryStats queries;
    private Double avgQueryTime;
    private Long slowQueries;
    private ConnectionStats connections;
    private Double cacheHitRate;
    private List<DbHistory> history;
    
    @Data
    public static class QueryStats {
        private Long total;
        private Long select;
        private Long insert;
        private Long update;
        private Long delete;
    }
    
    @Data
    public static class ConnectionStats {
        private Integer current;
        private Integer max;
    }
    
    @Data
    public static class DbHistory {
        private LocalDateTime time;
        private Long qps;
        private Double avgQueryTime;
    }
}
