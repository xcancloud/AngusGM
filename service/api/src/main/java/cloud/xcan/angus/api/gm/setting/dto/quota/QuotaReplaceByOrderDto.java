package cloud.xcan.angus.api.gm.setting.dto.quota;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class QuotaReplaceByOrderDto implements Serializable {

  @NotNull
  @Schema(description = "Order identifier for quota replacement. Used for identifying the specific order for quota management", requiredMode = RequiredMode.REQUIRED)
  private Long orderId;

  @NotEmpty
  @Schema(description = "Order resource quotas for replacement. Contains quota configurations for different resource types", requiredMode = RequiredMode.REQUIRED)
  private Set<QuotaReplaceDto> quotas;

  // TODO tenantId, status, expired ：Change to caller passing parameters

  @NotNull
  @Schema(description = "Order tenant identifier for quota management. Used for tenant-specific quota allocation", requiredMode = RequiredMode.REQUIRED)
  private Long tenantId;

  @NotEmpty
  @Schema(description = "Order status for quota processing. Used for order lifecycle management and quota validation", requiredMode = RequiredMode.REQUIRED)
  private String status;

  @NotNull
  @Schema(description = "Order expiration flag for quota management. Used for determining if order quotas are still valid", requiredMode = RequiredMode.REQUIRED)
  private Boolean expired;

}
