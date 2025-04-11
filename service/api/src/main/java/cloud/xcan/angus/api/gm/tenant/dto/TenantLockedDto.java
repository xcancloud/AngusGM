package cloud.xcan.angus.api.gm.tenant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class TenantLockedDto implements Serializable {

  @NotNull
  @Schema(description = "Tenant id.", example = "1", requiredMode = RequiredMode.REQUIRED)
  private Long id;

  @NotNull
  @Schema(description = "Tenant locked or unlocked flag.", example = "true", requiredMode = RequiredMode.REQUIRED)
  private Boolean locked;

  @Schema(description = "Tenant account locking start date.", example = "2022-04-01 12:00:00")
  private LocalDateTime lockStartDate;

  @Schema(description = "Tenant account locking end date.", example = "2023-06-01 12:00:00")
  private LocalDateTime lockEndDate;

}
