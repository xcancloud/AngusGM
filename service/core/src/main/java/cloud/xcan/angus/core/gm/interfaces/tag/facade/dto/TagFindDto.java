package cloud.xcan.angus.core.gm.interfaces.tag.facade.dto;

import cloud.xcan.angus.core.gm.domain.tag.enums.TagStatus;
import cloud.xcan.angus.spec.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Find tag DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "查询标签请求参数")
public class TagFindDto extends PageQuery {

  @Schema(description = "搜索关键词（名称）")
  private String keyword;

  @Schema(description = "状态筛选")
  private TagStatus status;

  @Schema(description = "分类筛选")
  private String category;

  @Schema(description = "排序字段", allowableValues = {"id", "createdDate", "modifiedDate", "name", "sortOrder"})
  private String orderBy = "sortOrder";
}
