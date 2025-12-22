package cloud.xcan.angus.core.gm.interfaces.department.facade.dto;

import cloud.xcan.angus.core.gm.domain.department.enums.DepartmentStatus;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Find department DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "查询部门请求参数")
public class DepartmentFindDto extends PageQuery {

  @Schema(description = "搜索关键词（名称、编码）")
  private String keyword;

  @Schema(description = "状态筛选")
  private DepartmentStatus status;

  @Schema(description = "父部门ID")
  private Long parentId;

  @Schema(description = "层级筛选")
  private Integer level;

  @Schema(description = "排序字段", allowableValues = {"id", "createdDate", "modifiedDate", "name", "sortOrder"})
  private String orderBy = "sortOrder";
}
