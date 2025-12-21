package cloud.xcan.angus.core.gm.interfaces.monitoring.facade.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProcessInfoVo {
    private Long pid;
    private String name;
    private String user;
    private Double cpuPercent;
    private Double memoryPercent;
    private String memoryUsage;
    private String status;
    private LocalDateTime startTime;
    private String command;
}
