package cloud.xcan.angus.core.gm.interfaces.department.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "部门路径响应")
public class DepartmentPathVo {

  @Schema(description = "路径字符串")
  private String path;

  @Schema(description = "路径数组")
  private List<PathItemVo> pathArray;

  @Data
  @Schema(description = "路径节点")
  public static class PathItemVo {

    @Schema(description = "部门ID")
    private Long id;

    @Schema(description = "部门名称")
    private String name;
  }
}
