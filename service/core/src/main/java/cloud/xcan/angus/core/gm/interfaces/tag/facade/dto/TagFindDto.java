package cloud.xcan.angus.core.gm.interfaces.tag.facade.dto;

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

  @Schema(description = "搜索关键词（名称、描述）")
  private String keyword;

  @Schema(description = "是否系统标签")
  private Boolean isSystem;

  @Schema(description = "排序字段", allowableValues = {"id", "createdDate", "modifiedDate", "name"})
  private String orderBy = "id";
}
