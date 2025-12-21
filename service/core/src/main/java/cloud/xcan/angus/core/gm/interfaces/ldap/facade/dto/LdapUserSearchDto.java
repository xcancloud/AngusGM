package cloud.xcan.angus.core.gm.interfaces.ldap.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "LDAP用户搜索DTO")
public class LdapUserSearchDto implements Serializable {
    
    @Schema(description = "搜索关键词")
    private String keyword;
    
    @Schema(description = "搜索基础")
    private String searchBase;
    
    @Schema(description = "搜索过滤器")
    private String searchFilter;
    
    @Schema(description = "返回数量限制", example = "50")
    private Integer limit = 50;
}
