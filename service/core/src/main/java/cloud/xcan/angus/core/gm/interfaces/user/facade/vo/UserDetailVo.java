package cloud.xcan.angus.core.gm.interfaces.user.facade.vo;

import cloud.xcan.angus.core.gm.domain.user.enums.EnableStatus;
import cloud.xcan.angus.core.gm.domain.user.enums.UserAccountType;
import cloud.xcan.angus.core.gm.domain.user.enums.UserStatus;
import cloud.xcan.angus.spec.TenantAuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * User detail VO
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "用户详情")
public class UserDetailVo extends TenantAuditingVo {

  @Schema(description = "ID")
  private Long id;

  @Schema(description = "用户名")
  private String username;

  @Schema(description = "姓名")
  private String name;

  @Schema(description = "邮箱")
  private String email;

  @Schema(description = "手机号")
  private String phone;

  @Schema(description = "头像")
  private String avatar;

  @Schema(description = "角色")
  private String role;

  @Schema(description = "角色ID列表")
  private List<Long> roleIds;

  @Schema(description = "部门")
  private String department;

  @Schema(description = "部门ID")
  private Long departmentId;

  @Schema(description = "部门路径")
  private String departmentPath;

  @Schema(description = "状态")
  private UserStatus status;

  @Schema(description = "启用状态")
  private EnableStatus enableStatus;

  @Schema(description = "账号类型")
  private UserAccountType accountType;

  @Schema(description = "是否锁定")
  private Boolean isLocked;

  @Schema(description = "是否在线")
  private Boolean isOnline;

  @Schema(description = "最后登录时间")
  private LocalDateTime lastLogin;

  @Schema(description = "角色详情列表")
  private List<RoleVo> roles;

  @Schema(description = "用户组列表")
  private List<GroupVo> groups;

  @Schema(description = "登录历史")
  private List<LoginHistoryVo> loginHistory;

  /**
   * Role detail VO
   */
  @Data
  @Schema(description = "角色详情")
  public static class RoleVo {
    @Schema(description = "角色ID")
    private Long id;
    @Schema(description = "角色名称")
    private String name;
    @Schema(description = "角色编码")
    private String code;
  }

  /**
   * Group VO
   */
  @Data
  @Schema(description = "用户组")
  public static class GroupVo {
    @Schema(description = "组ID")
    private Long id;
    @Schema(description = "组名称")
    private String name;
  }

  /**
   * Login history VO
   */
  @Data
  @Schema(description = "登录历史")
  public static class LoginHistoryVo {
    @Schema(description = "登录时间")
    private LocalDateTime time;
    @Schema(description = "IP地址")
    private String ip;
    @Schema(description = "登录地点")
    private String location;
  }
}
