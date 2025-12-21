package cloud.xcan.angus.core.gm.interfaces.department.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "转移部门成员请求参数")
public class DepartmentMemberTransferDto {

  @NotNull
  @Schema(description = "目标部门ID", requiredMode = RequiredMode.REQUIRED)
  private Long targetDepartmentId;

  @NotEmpty
  @Schema(description = "用户ID列表")
  private List<Long> userIds;
}
