package cloud.xcan.angus.core.gm.interfaces.systemversion.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 变更日志查询DTO
 */
@Data
@Schema(description = "变更日志查询请求")
public class ChangelogFindDto {
    
    @Schema(description = "指定版本号", example = "1.5.2")
    private String version;
    
    @Schema(description = "页码", example = "1")
    private Integer page = 1;
    
    @Schema(description = "每页大小", example = "10")
    private Integer size = 10;
}
