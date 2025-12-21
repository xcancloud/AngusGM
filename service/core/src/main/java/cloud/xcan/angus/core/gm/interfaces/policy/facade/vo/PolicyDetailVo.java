package cloud.xcan.angus.core.gm.interfaces.policy.facade.vo;

import cloud.xcan.angus.spec.TenantAuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Role detail VO
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "角色详情")
public class PolicyDetailVo extends TenantAuditingVo {

  @Schema(description = "ID")
  private Long id;

  @Schema(description = "角色名称")
  private String name;

  @Schema(description = "角色编码")
  private String code;

  @Schema(description = "描述")
  private String description;

  @Schema(description = "是否系统角色")
  private Boolean isSystem;

  @Schema(description = "是否默认角色")
  private Boolean isDefault;

  @Schema(description = "用户数量")
  private Long userCount;

  @Schema(description = "应用ID")
  private String appId;

  @Schema(description = "应用名称")
  private String appName;

  @Schema(description = "权限列表")
  private List<PermissionVo> permissions;

  @Schema(description = "关联用户列表")
  private List<RoleUserVo> users;

  @Data
  @Schema(description = "权限信息")
  public static class PermissionVo {

    @Schema(description = "资源标识")
    private String resource;

    @Schema(description = "资源名称")
    private String resourceName;

    @Schema(description = "操作列表")
    private List<String> actions;
  }

  @Data
  @Schema(description = "角色用户信息")
  public static class RoleUserVo {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名")
    private String name;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "头像")
    private String avatar;
  }
}
