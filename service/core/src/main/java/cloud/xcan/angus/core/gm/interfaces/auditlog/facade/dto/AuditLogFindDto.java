package cloud.xcan.angus.core.gm.interfaces.auditlog.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "审计日志查询DTO")
public class AuditLogFindDto implements Serializable {
    
    @Schema(description = "页码", example = "1")
    private Integer page = 1;
    
    @Schema(description = "每页大小", example = "20")
    private Integer size = 20;
    
    @Schema(description = "搜索关键词（操作描述）")
    private String keyword;
    
    @Schema(description = "操作人ID筛选")
    private String userId;
    
    @Schema(description = "模块筛选（users、tenants、permissions等）")
    private String module;
    
    @Schema(description = "操作类型（create、update、delete、query等）")
    private String operation;
    
    @Schema(description = "日志级别（info、warning、error）")
    private String level;
    
    @Schema(description = "IP地址筛选")
    private String ipAddress;
    
    @Schema(description = "开始日期")
    private String startDate;
    
    @Schema(description = "结束日期")
    private String endDate;
}
