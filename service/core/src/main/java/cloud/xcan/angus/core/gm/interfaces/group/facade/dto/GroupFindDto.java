package cloud.xcan.angus.core.gm.interfaces.group.facade.dto;

import cloud.xcan.angus.core.gm.domain.group.enums.GroupStatus;
import cloud.xcan.angus.core.gm.domain.group.enums.GroupType;
import cloud.xcan.angus.spec.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Find group DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "查询组请求参数")
public class GroupFindDto extends PageQuery {

  @Schema(description = "搜索关键词（名称、编码）")
  private String keyword;

  @Schema(description = "状态筛选")
  private GroupStatus status;

  @Schema(description = "类型筛选")
  private GroupType type;

  @Schema(description = "负责人ID")
  private Long ownerId;

  @Schema(description = "排序字段", allowableValues = {"id", "createdDate", "modifiedDate", "name"})
  private String orderBy = "createdDate";
}
