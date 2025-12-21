package cloud.xcan.angus.core.gm.interfaces.ldap.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "LDAP配置更新DTO")
public class LdapConfigUpdateDto implements Serializable {
    
    @Schema(description = "是否启用")
    private Boolean isEnabled;
    
    @NotBlank
    @Schema(description = "LDAP服务器地址")
    private String server;
    
    @NotBlank
    @Schema(description = "基础DN")
    private String baseDN;
    
    @NotBlank
    @Schema(description = "绑定DN")
    private String bindDN;
    
    @Schema(description = "绑定密码")
    private String bindPassword;
    
    @Schema(description = "用户搜索基础")
    private String userSearchBase;
    
    @Schema(description = "用户搜索过滤器")
    private String userSearchFilter;
    
    @Schema(description = "组搜索基础")
    private String groupSearchBase;
    
    @Schema(description = "组搜索过滤器")
    private String groupSearchFilter;
    
    @Schema(description = "是否使用SSL")
    private Boolean useSsl;
    
    @Schema(description = "是否启用同步")
    private Boolean syncEnabled;
    
    @Schema(description = "同步间隔（秒）")
    private Integer syncInterval;
}
