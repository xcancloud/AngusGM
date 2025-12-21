package cloud.xcan.angus.core.gm.interfaces.ldap.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "LDAP用户VO")
public class LdapUserVo implements Serializable {
    
    @Schema(description = "用户DN")
    private String dn;
    
    @Schema(description = "用户ID")
    private String uid;
    
    @Schema(description = "通用名称")
    private String cn;
    
    @Schema(description = "邮箱")
    private String mail;
    
    @Schema(description = "部门")
    private String department;
    
    @Schema(description = "职位")
    private String title;
    
    @Schema(description = "手机号")
    private String mobile;
}
