package cloud.xcan.angus.core.gm.interfaces.tenant.facade.dto;

import cloud.xcan.angus.spec.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Find tenant DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "查询租户请求参数")
public class TenantFindDto extends PageQuery {

  @Schema(description = "搜索关键词（名称、编码）")
  private String keyword;

  @Schema(description = "状态筛选（已启用、已禁用）")
  private String status;

  @Schema(description = "类型筛选（个人、企业）")
  private String type;

  @Schema(description = "账号类型（main、sub）")
  private String accountType;

  @Schema(description = "排序字段", allowableValues = {"id", "createdDate", "modifiedDate", "name"})
  private String orderBy = "createdDate";
}
