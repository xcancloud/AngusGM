package cloud.xcan.angus.core.gm.interfaces.department.facade.dto;

import cloud.xcan.angus.core.gm.domain.department.enums.DepartmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Update department DTO
 */
@Data
@Schema(description = "更新部门请求参数")
public class DepartmentUpdateDto {

  @Size(max = 100)
  @Schema(description = "部门名称")
  private String name;

  @Schema(description = "父部门ID")
  private Long parentId;

  @Schema(description = "负责人ID")
  private Long leaderId;

  @Size(max = 500)
  @Schema(description = "描述")
  private String description;

  @Schema(description = "排序")
  private Integer sortOrder;

  @Schema(description = "状态")
  private DepartmentStatus status;
}
