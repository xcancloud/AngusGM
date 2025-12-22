package cloud.xcan.angus.core.gm.interfaces.group.facade.dto;

import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Group member find DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "查询组成员请求参数")
public class GroupMemberFindDto extends PageQuery {

  @Schema(description = "搜索关键词")
  private String keyword;
}
