package cloud.xcan.angus.core.gm.interfaces.setting.facade.dto;

import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class TenantQuotaFindDto extends PageQuery {

  @Schema(description = "Tenant quota unique identifier for filtering")
  private Long id;

  @Schema(description = "Application code for filtering quotas by application")
  private String appCode;

  @Schema(hidden = true)
  private String serviceCode;

  @Schema(description = "Tenant quota resource name for searching")
  private String name;

  @Schema(description = "Quota modification permission flag for filtering")
  private Boolean allowChange;

}
