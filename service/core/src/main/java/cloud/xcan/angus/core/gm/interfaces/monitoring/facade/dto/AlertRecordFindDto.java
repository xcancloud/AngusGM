package cloud.xcan.angus.core.gm.interfaces.monitoring.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "告警记录查询DTO")
public class AlertRecordFindDto {
    
    @Schema(description = "页码", example = "1")
    private Integer page = 1;
    
    @Schema(description = "每页大小", example = "10")
    private Integer size = 10;
    
    @Schema(description = "等级筛选（低、中、高）")
    private String level;
    
    @Schema(description = "状态筛选（待处理、已处理、已忽略）")
    private String status;
    
    @Schema(description = "开始日期")
    private LocalDateTime startDate;
    
    @Schema(description = "结束日期")
    private LocalDateTime endDate;
}
