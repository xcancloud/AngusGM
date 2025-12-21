package cloud.xcan.angus.core.gm.interfaces.quota.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "租户配额查询DTO")
public class TenantQuotaFindDto implements Serializable {
    
    @Schema(description = "页码", example = "1")
    private Integer page = 1;
    
    @Schema(description = "每页大小", example = "10")
    private Integer size = 10;
    
    @Schema(description = "搜索关键词（租户名称）")
    private String keyword;
    
    @Schema(description = "状态筛选（正常、接近上限、已超限）")
    private String status;
}
