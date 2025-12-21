package cloud.xcan.angus.core.gm.interfaces.backup.facade.vo;

import lombok.Data;

@Data
public class RestoreProgressVo {
    private String restoreId;
    private String status;
    private Integer progress;
    private String currentStep;
    private Integer totalSteps;
    private Integer completedSteps;
    private java.time.LocalDateTime startTime;
    private java.time.LocalDateTime estimatedEndTime;
}