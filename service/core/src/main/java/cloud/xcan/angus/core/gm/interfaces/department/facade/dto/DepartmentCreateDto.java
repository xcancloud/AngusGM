package cloud.xcan.angus.core.gm.interfaces.department.facade.dto;

import cloud.xcan.angus.core.gm.domain.department.enums.DepartmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Create department DTO
 */
@Data
@Schema(description = "创建部门请求参数")
public class DepartmentCreateDto {

  @NotBlank
  @Size(max = 100)
  @Schema(description = "部门名称", requiredMode = RequiredMode.REQUIRED)
  private String name;

  @NotBlank
  @Size(max = 50)
  @Schema(description = "部门编码", requiredMode = RequiredMode.REQUIRED)
  private String code;

  @Schema(description = "父部门ID")
  private Long parentId;

  @Schema(description = "负责人ID")
  private Long managerId;

  @Size(max = 500)
  @Schema(description = "描述")
  private String description;

  @Schema(description = "排序")
  private Integer sortOrder;

  @Schema(description = "状态")
  private DepartmentStatus status;
}
