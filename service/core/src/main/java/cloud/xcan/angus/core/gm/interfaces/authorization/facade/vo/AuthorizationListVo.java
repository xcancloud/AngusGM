package cloud.xcan.angus.core.gm.interfaces.authorization.facade.vo;

import cloud.xcan.angus.spec.TenantAuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Authorization List VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "授权列表VO")
public class AuthorizationListVo extends TenantAuditingVo {
    
    @Schema(description = "授权ID")
    private Long id;
    
    @Schema(description = "目标类型（user、department、group）")
    private String targetType;
    
    @Schema(description = "目标ID")
    private Long targetId;
    
    @Schema(description = "目标名称")
    private String targetName;
    
    @Schema(description = "目标头像（用户）")
    private String targetAvatar;
    
    @Schema(description = "目标部门（用户）")
    private String targetDepartment;
    
    @Schema(description = "目标邮箱（用户）")
    private String targetEmail;
    
    @Schema(description = "上级部门（部门）")
    private String targetParent;
    
    @Schema(description = "用户数量（部门/组）")
    private Integer targetUserCount;
    
    @Schema(description = "描述（组）")
    private String targetDescription;
    
    @Schema(description = "角色列表")
    private List<RoleInfo> roles;
    
    @Data
    @Schema(description = "角色信息")
    public static class RoleInfo {
        @Schema(description = "角色ID")
        private Long id;
        
        @Schema(description = "角色名称")
        private String name;
        
        @Schema(description = "角色编码")
        private String code;
        
        @Schema(description = "应用ID")
        private Long appId;
        
        @Schema(description = "应用名称")
        private String appName;
    }
}
