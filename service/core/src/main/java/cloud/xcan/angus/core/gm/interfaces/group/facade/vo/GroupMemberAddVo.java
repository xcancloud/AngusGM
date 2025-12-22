package cloud.xcan.angus.core.gm.interfaces.group.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

/**
 * Group member add response VO
 */
@Data
@Schema(description = "添加组成员响应")
public class GroupMemberAddVo {

  @Schema(description = "组ID")
  private Long groupId;

  @Schema(description = "添加数量")
  private Integer addedCount;

  @Schema(description = "已添加用户列表")
  private List<AddedUserVo> addedUsers;

  @Data
  @Schema(description = "已添加用户")
  public static class AddedUserVo {
    @Schema(description = "用户ID")
    private Long id;
    
    @Schema(description = "用户姓名")
    private String name;
  }
}
