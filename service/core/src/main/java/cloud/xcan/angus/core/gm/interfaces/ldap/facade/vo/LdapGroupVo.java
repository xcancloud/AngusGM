package cloud.xcan.angus.core.gm.interfaces.ldap.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "LDAP组VO")
public class LdapGroupVo implements Serializable {
    
    @Schema(description = "组DN")
    private String dn;
    
    @Schema(description = "组通用名称")
    private String cn;
    
    @Schema(description = "描述")
    private String description;
    
    @Schema(description = "成员数量")
    private Integer memberCount;
}
