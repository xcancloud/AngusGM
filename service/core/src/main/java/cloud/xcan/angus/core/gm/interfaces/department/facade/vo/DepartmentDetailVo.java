package cloud.xcan.angus.core.gm.interfaces.department.facade.vo;

import cloud.xcan.angus.core.gm.domain.department.enums.DepartmentStatus;
import cloud.xcan.angus.spec.TenantAuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Department detail VO
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "部门详情")
public class DepartmentDetailVo extends TenantAuditingVo {

  @Schema(description = "ID")
  private Long id;

  @Schema(description = "部门名称")
  private String name;

  @Schema(description = "部门编码")
  private String code;

  @Schema(description = "父部门ID")
  private Long parentId;

  @Schema(description = "父部门名称")
  private String parentName;

  @Schema(description = "层级")
  private Integer level;

  @Schema(description = "排序")
  private Integer sortOrder;

  @Schema(description = "负责人ID")
  private Long leaderId;

  @Schema(description = "负责人姓名")
  private String leaderName;

  @Schema(description = "描述")
  private String description;

  @Schema(description = "状态")
  private DepartmentStatus status;

  @Schema(description = "成员数量")
  private Long memberCount;

  @Schema(description = "子部门列表")
  private List<DepartmentDetailVo> children;
}
