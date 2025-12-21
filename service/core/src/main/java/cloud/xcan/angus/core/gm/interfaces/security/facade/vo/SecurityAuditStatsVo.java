package cloud.xcan.angus.core.gm.interfaces.security.facade.vo;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class SecurityAuditStatsVo {
    private Period period;
    private Long totalEvents;
    private Long highRiskEvents;
    private Long mediumRiskEvents;
    private Long lowRiskEvents;
    private Long loginFailures;
    private Long passwordChanges;
    private Long permissionChanges;
    private List<DayCount> eventsByDay;
    
    @Data
    public static class Period {
        private LocalDate startDate;
        private LocalDate endDate;
    }
    
    @Data
    public static class DayCount {
        private LocalDate date;
        private Long count;
    }
}
