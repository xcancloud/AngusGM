package cloud.xcan.angus.core.gm.interfaces.department.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Department list VO
 */
@Data
@Schema(description = "部门列表项")
public class DepartmentListVo extends DepartmentDetailVo {
  // Inherits all fields from DepartmentDetailVo
}
