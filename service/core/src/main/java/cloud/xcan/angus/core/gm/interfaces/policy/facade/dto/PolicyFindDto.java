package cloud.xcan.angus.core.gm.interfaces.policy.facade.dto;

import cloud.xcan.angus.spec.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Find role DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "查询角色请求参数")
public class PolicyFindDto extends PageQuery {

  @Schema(description = "搜索关键词（名称、编码）")
  private String keyword;

  @Schema(description = "应用ID筛选")
  private String appId;

  @Schema(description = "是否系统角色")
  private Boolean isSystem;

  @Schema(description = "是否默认角色")
  private Boolean isDefault;

  @Schema(description = "排序字段", allowableValues = {"id", "createdDate", "modifiedDate", "name"})
  private String orderBy = "id";
}
