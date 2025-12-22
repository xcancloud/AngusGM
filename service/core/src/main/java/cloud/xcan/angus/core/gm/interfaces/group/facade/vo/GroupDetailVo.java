package cloud.xcan.angus.core.gm.interfaces.group.facade.vo;

import cloud.xcan.angus.core.gm.domain.group.enums.GroupStatus;
import cloud.xcan.angus.core.gm.domain.group.enums.GroupType;
import cloud.xcan.angus.spec.TenantAuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Group detail VO
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "组详情")
public class GroupDetailVo extends TenantAuditingVo {

  @Schema(description = "ID")
  private Long id;

  @Schema(description = "组名称")
  private String name;

  @Schema(description = "组编码")
  private String code;

  @Schema(description = "描述")
  private String description;

  @Schema(description = "组类型")
  private GroupType type;

  @Schema(description = "状态")
  private GroupStatus status;

  @Schema(description = "负责人ID")
  private Long ownerId;

  @Schema(description = "负责人姓名")
  private String ownerName;

  @Schema(description = "负责人头像")
  private String ownerAvatar;

  @Schema(description = "成员数量")
  private Long memberCount;

  @Schema(description = "成员ID列表")
  private List<Long> memberIds;

  @Schema(description = "标签列表")
  private List<String> tags;

  @Schema(description = "最后活跃时间")
  private String lastActive;

  @Schema(description = "成员列表")
  private List<GroupMemberVo> members;
}
