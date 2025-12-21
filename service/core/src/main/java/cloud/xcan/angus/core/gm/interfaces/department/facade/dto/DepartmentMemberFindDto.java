package cloud.xcan.angus.core.gm.interfaces.department.facade.dto;

import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "查询部门成员请求参数")
public class DepartmentMemberFindDto extends PageQuery {

  @Schema(description = "搜索关键词")
  private String keyword;
}
