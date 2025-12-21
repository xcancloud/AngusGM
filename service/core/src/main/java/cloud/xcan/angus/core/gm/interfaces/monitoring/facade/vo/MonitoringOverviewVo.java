package cloud.xcan.angus.core.gm.interfaces.monitoring.facade.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MonitoringOverviewVo {
    private String systemStatus;
    private Double cpuUsage;
    private Double memoryUsage;
    private Double diskUsage;
    private String networkIn;
    private String networkOut;
    private Long activeConnections;
    private String uptime;
    private LocalDateTime lastUpdateTime;
}
