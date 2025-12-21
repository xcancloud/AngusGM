package cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto;

import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Authorization Find DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "查询授权请求参数")
public class AuthorizationFindDto extends PageQuery {
    
    @Schema(description = "目标类型（user、department、group）")
    private String targetType;
    
    @Schema(description = "应用ID筛选")
    private Long appId;
    
    @Schema(description = "角色ID筛选")
    private Long roleId;
    
    @Schema(description = "搜索关键词")
    private String keyword;
}
