package cloud.xcan.angus.core.gm.interfaces.tenant.facade.dto;

import cloud.xcan.angus.core.gm.domain.tenant.enums.AccountType;
import cloud.xcan.angus.core.gm.domain.tenant.enums.TenantStatus;
import cloud.xcan.angus.core.gm.domain.tenant.enums.TenantType;
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

  @Schema(description = "状态筛选")
  private TenantStatus status;

  @Schema(description = "类型筛选")
  private TenantType type;

  @Schema(description = "账号类型")
  private AccountType accountType;

  @Schema(description = "排序字段", allowableValues = {"id", "createdDate", "modifiedDate", "name"})
  private String orderBy = "createdDate";
}
