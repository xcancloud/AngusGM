package cloud.xcan.angus.core.gm.interfaces.ldap.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "LDAP认证测试DTO")
public class LdapAuthTestDto implements Serializable {
    
    @NotBlank
    @Schema(description = "用户名")
    private String username;
    
    @NotBlank
    @Schema(description = "密码")
    private String password;
}
