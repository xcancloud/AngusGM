package cloud.xcan.angus.core.gm.interfaces.backup.facade.vo;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "恢复进度信息")
public class RestoreProgressVo {
    @Schema(description = "恢复任务ID")
    private Long restoreId;
    
    @Schema(description = "状态")
    private String status;
    
    @Schema(description = "进度百分比")
    private Integer progress;
    
    @Schema(description = "当前步骤")
    private String currentStep;
    
    @Schema(description = "总步骤数")
    private Integer totalSteps;
    
    @Schema(description = "已完成步骤数")
    private Integer completedSteps;
    
    @Schema(description = "开始时间")
    private java.time.LocalDateTime startTime;
    
    @Schema(description = "预计结束时间")
    private java.time.LocalDateTime estimatedEndTime;
}