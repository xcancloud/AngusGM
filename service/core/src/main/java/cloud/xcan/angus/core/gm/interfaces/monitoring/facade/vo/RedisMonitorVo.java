package cloud.xcan.angus.core.gm.interfaces.monitoring.facade.vo;

import lombok.Data;

@Data
public class RedisMonitorVo {
    private String status;
    private String version;
    private String uptime;
    private Integer connectedClients;
    private Integer maxClients;
    private String usedMemory;
    private String maxMemory;
    private Double memoryUsagePercent;
    private Double hitRate;
    private Long ops;
    private Long keyspaceHits;
    private Long keyspaceMisses;
}
