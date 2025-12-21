package cloud.xcan.angus.core.gm.interfaces.monitoring.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "进程查询DTO")
public class ProcessFindDto {
    
    @Schema(description = "页码", example = "1")
    private Integer page = 1;
    
    @Schema(description = "每页大小", example = "20")
    private Integer size = 20;
    
    @Schema(description = "排序字段（cpu、memory、name）")
    private String sortBy = "cpu";
    
    @Schema(description = "排序方向（asc、desc）")
    private String order = "desc";
}
