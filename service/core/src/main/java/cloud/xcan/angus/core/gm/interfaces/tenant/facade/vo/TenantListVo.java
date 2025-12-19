package cloud.xcan.angus.core.gm.interfaces.tenant.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Tenant list VO
 */
@Data
@Schema(description = "租户列表项")
public class TenantListVo extends TenantDetailVo {
  // Inherits all fields from TenantDetailVo
}
