package cloud.xcan.angus.core.gm.interfaces.systemversion.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 版本历史查询DTO
 */
@Data
@Schema(description = "版本历史查询请求")
public class VersionHistoryFindDto {
    
    @Schema(description = "页码", example = "1")
    private Integer page = 1;
    
    @Schema(description = "每页大小", example = "10")
    private Integer size = 10;
}
