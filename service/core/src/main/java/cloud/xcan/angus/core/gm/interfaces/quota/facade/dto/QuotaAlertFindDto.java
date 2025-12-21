package cloud.xcan.angus.core.gm.interfaces.quota.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "配额告警查询DTO")
public class QuotaAlertFindDto implements Serializable {
    
    @Schema(description = "页码", example = "1")
    private Integer page = 1;
    
    @Schema(description = "每页大小", example = "10")
    private Integer size = 10;
    
    @Schema(description = "租户ID筛选")
    private String tenantId;
    
    @Schema(description = "资源类型（users、storage、applications、apiCalls）")
    private String resourceType;
    
    @Schema(description = "告警级别（warning、critical）")
    private String level;
    
    @Schema(description = "状态筛选（待处理、已处理）")
    private String status;
}
