package cloud.xcan.angus.core.gm.interfaces.monitoring.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "告警规则查询DTO")
public class AlertRuleFindDto {
    
    @Schema(description = "页码", example = "1")
    private Integer page = 1;
    
    @Schema(description = "每页大小", example = "10")
    private Integer size = 10;
    
    @Schema(description = "状态筛选（启用、禁用）")
    private String status;
}
