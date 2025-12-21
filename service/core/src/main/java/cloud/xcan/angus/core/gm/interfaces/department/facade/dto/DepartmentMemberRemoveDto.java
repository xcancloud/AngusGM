package cloud.xcan.angus.core.gm.interfaces.department.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "批量移除部门成员请求参数")
public class DepartmentMemberRemoveDto {

  @NotEmpty
  @Schema(description = "用户ID列表")
  private List<Long> userIds;
}
