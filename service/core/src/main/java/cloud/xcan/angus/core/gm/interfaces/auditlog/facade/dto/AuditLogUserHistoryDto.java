package cloud.xcan.angus.core.gm.interfaces.auditlog.facade.dto;

import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "审计日志用户历史查询DTO")
public class AuditLogUserHistoryDto extends PageQuery {
    
    @Schema(description = "开始日期")
    private String startDate;
    
    @Schema(description = "结束日期")
    private String endDate;
}
