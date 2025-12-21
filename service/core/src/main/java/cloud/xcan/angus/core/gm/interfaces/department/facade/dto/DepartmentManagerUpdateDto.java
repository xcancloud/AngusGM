package cloud.xcan.angus.core.gm.interfaces.department.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "设置部门负责人请求参数")
public class DepartmentManagerUpdateDto {

  @NotNull
  @Schema(description = "负责人用户ID", requiredMode = RequiredMode.REQUIRED)
  private Long managerId;
}
