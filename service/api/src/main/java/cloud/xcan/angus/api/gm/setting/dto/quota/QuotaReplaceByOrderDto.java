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
  @Schema(description = "Order id.", requiredMode = RequiredMode.REQUIRED)
  private Long orderId;

  @NotEmpty
  @Schema(description = "Order resource quotas.", requiredMode = RequiredMode.REQUIRED)
  private Set<QuotaReplaceDto> quotas;

  // TODO tenantId, status, expired ：Change to caller passing parameters

  @NotNull
  @Schema(description = "Order tenant id.", requiredMode = RequiredMode.REQUIRED)
  private Long tenantId;

  @NotEmpty
  @Schema(description = "Order status.", requiredMode = RequiredMode.REQUIRED)
  private String status;

  @NotNull
  @Schema(description = "Order expired flag.", requiredMode = RequiredMode.REQUIRED)
  private Boolean expired;

}
