package cloud.xcan.angus.core.gm.interfaces.ldap.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "LDAP连接测试DTO")
public class LdapConnectionTestDto implements Serializable {
    
    @NotBlank
    @Schema(description = "LDAP服务器地址")
    private String server;
    
    @NotBlank
    @Schema(description = "基础DN")
    private String baseDN;
    
    @NotBlank
    @Schema(description = "绑定DN")
    private String bindDN;
    
    @NotBlank
    @Schema(description = "绑定密码")
    private String bindPassword;
    
    @Schema(description = "是否使用SSL")
    private Boolean useSsl;
}
