package cloud.xcan.angus.core.gm.interfaces.auditlog.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "审计日志导出DTO")
public class AuditLogExportDto implements Serializable {
    
    @Schema(description = "模块筛选")
    private String module;
    
    @Schema(description = "操作类型筛选")
    private String operation;
    
    @Schema(description = "日志级别筛选")
    private String level;
    
    @Schema(description = "开始日期")
    private String startDate;
    
    @Schema(description = "结束日期")
    private String endDate;
    
    @Schema(description = "导出格式（excel、csv）")
    private String format = "excel";
}
