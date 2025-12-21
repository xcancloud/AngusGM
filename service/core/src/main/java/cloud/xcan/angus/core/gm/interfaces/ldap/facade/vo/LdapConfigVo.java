package cloud.xcan.angus.core.gm.interfaces.ldap.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "LDAP配置VO")
public class LdapConfigVo implements Serializable {
    
    @Schema(description = "配置ID")
    private String id;
    
    @Schema(description = "是否启用")
    private Boolean isEnabled;
    
    @Schema(description = "LDAP服务器地址")
    private String server;
    
    @Schema(description = "基础DN")
    private String baseDN;
    
    @Schema(description = "绑定DN")
    private String bindDN;
    
    @Schema(description = "绑定密码（脱敏）")
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
    
    @Schema(description = "最后同步时间")
    private LocalDateTime lastSyncTime;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdDate;
    
    @Schema(description = "修改时间")
    private LocalDateTime modifiedDate;
}
