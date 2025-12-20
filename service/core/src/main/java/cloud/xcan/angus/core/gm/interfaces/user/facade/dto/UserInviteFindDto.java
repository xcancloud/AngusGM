package cloud.xcan.angus.core.gm.interfaces.user.facade.dto;

import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Query user invites DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "查询邀请列表请求参数")
public class UserInviteFindDto extends PageQuery {

  @Schema(description = "搜索关键词（邮箱）")
  private String keyword;

  @Schema(description = "状态筛选（待接受、已过期、已接受、已取消）")
  private String status;
}
