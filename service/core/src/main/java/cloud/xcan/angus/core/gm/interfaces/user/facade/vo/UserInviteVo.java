package cloud.xcan.angus.core.gm.interfaces.user.facade.vo;

import cloud.xcan.angus.core.gm.domain.user.enums.InviteStatus;
import cloud.xcan.angus.spec.TenantAuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * User invite VO
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "用户邀请响应")
public class UserInviteVo extends TenantAuditingVo {

  @Schema(description = "邀请ID")
  private Long id;

  @Schema(description = "邮箱")
  private String email;

  @Schema(description = "角色ID")
  private Long roleId;

  @Schema(description = "角色名称")
  private String roleName;

  @Schema(description = "部门ID")
  private Long departmentId;

  @Schema(description = "部门名称")
  private String departmentName;

  @Schema(description = "邀请人id")
  private Long invitedBy;

  @Schema(description = "邀请人姓名")
  private String inviterName;

  @Schema(description = "邀请时间")
  private LocalDateTime inviteDate;

  @Schema(description = "过期时间")
  private LocalDateTime expiryDate;

  @Schema(description = "状态")
  private InviteStatus status;

  @Schema(description = "邀请码")
  private String inviteCode;

  @Schema(description = "邀请链接")
  private String inviteUrl;
}
