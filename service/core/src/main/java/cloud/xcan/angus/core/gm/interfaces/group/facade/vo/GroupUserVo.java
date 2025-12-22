package cloud.xcan.angus.core.gm.interfaces.group.facade.vo;

import cloud.xcan.angus.spec.TenantAuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * User's group list VO
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "用户所在的组列表VO")
public class GroupUserVo extends TenantAuditingVo {

  @Schema(description = "组ID")
  private Long id;

  @Schema(description = "组名称")
  private String name;

  @Schema(description = "组类型")
  private String type;

  @Schema(description = "负责人姓名")
  private String ownerName;

  @Schema(description = "成员数量")
  private Long memberCount;

  @Schema(description = "状态")
  private String status;

  @Schema(description = "角色")
  private String role;
}
