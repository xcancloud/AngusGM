package cloud.xcan.angus.core.gm.interfaces.security.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "IP白名单查询DTO")
public class IpWhitelistFindDto {
    
    @Schema(description = "页码", example = "1")
    private Integer page = 1;
    
    @Schema(description = "每页大小", example = "10")
    private Integer size = 10;
    
    @Schema(description = "搜索关键词（IP地址、描述）")
    private String keyword;
    
    @Schema(description = "状态筛选（启用、禁用）")
    private String status;
}
