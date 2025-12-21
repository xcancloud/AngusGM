package cloud.xcan.angus.core.gm.interfaces.policy.facade.vo;

import cloud.xcan.angus.spec.TenantAuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Policy user VO
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "角色用户信息")
public class PolicyUserVo extends TenantAuditingVo {

  @Schema(description = "用户ID")
  private Long id;

  @Schema(description = "用户名")
  private String name;

  @Schema(description = "邮箱")
  private String email;

  @Schema(description = "头像")
  private String avatar;

  @Schema(description = "部门")
  private String department;

  @Schema(description = "状态")
  private String status;
}
