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
  @Schema(description = "Tenant identifier for lock management. Used for identifying the specific tenant for lock operations", example = "1", requiredMode = RequiredMode.REQUIRED)
  private Long id;

  @NotNull
  @Schema(description = "Tenant lock status flag for account security. Used for enabling or disabling tenant account access", example = "true", requiredMode = RequiredMode.REQUIRED)
  private Boolean locked;

  @Schema(description = "Tenant account lock start date for security tracking. Used for lock period management and monitoring", example = "2022-04-01 12:00:00")
  private LocalDateTime lockStartDate;

  @Schema(description = "Tenant account lock end date for security tracking. Used for lock period management and monitoring", example = "2023-06-01 12:00:00")
  private LocalDateTime lockEndDate;

}
