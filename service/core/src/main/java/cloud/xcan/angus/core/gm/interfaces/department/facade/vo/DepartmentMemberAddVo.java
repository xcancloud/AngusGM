package cloud.xcan.angus.core.gm.interfaces.department.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "添加部门成员响应")
public class DepartmentMemberAddVo {

  @Schema(description = "部门ID")
  private Long departmentId;

  @Schema(description = "添加成功数量")
  private Integer addedCount;

  @Schema(description = "添加的用户列表")
  private List<AddedUserVo> addedUsers;

  @Data
  @Schema(description = "添加的用户")
  public static class AddedUserVo {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名称")
    private String name;
  }
}
