package cloud.xcan.angus.core.gm.interfaces.auditlog.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "失败操作日志查询DTO")
public class AuditLogFailureFindDto implements Serializable {
    
    @Schema(description = "页码", example = "1")
    private Integer page = 1;
    
    @Schema(description = "每页大小", example = "20")
    private Integer size = 20;
    
    @Schema(description = "开始日期")
    private String startDate;
    
    @Schema(description = "结束日期")
    private String endDate;
}
