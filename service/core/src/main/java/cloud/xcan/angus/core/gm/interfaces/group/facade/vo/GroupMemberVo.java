package cloud.xcan.angus.core.gm.interfaces.group.facade.vo;

import cloud.xcan.angus.spec.TenantAuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Group member VO
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "组成员VO")
public class GroupMemberVo extends TenantAuditingVo {

  @Schema(description = "用户ID")
  private Long id;

  @Schema(description = "用户姓名")
  private String name;

  @Schema(description = "邮箱")
  private String email;

  @Schema(description = "头像")
  private String avatar;

  @Schema(description = "部门")
  private String department;

  @Schema(description = "部门ID")
  private Long departmentId;

  @Schema(description = "角色")
  private String role;

  @Schema(description = "加入时间")
  private LocalDateTime joinDate;

  @Schema(description = "是否负责人")
  private Boolean isOwner;
}
