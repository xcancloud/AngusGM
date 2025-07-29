package cloud.xcan.angus.core.gm.interfaces.user.facade.dto;

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
public class UserLockedDto implements Serializable {

  @NotNull
  @Schema(description = "Unique identifier of the user account", requiredMode = RequiredMode.REQUIRED)
  private Long id;

  @NotNull
  @Schema(description = "Account lock status (true to lock, false to unlock)", requiredMode = RequiredMode.REQUIRED)
  private Boolean locked;

  @Schema(description = "Account lock start time")
  private LocalDateTime lockStartDate;

  @Schema(description = "Account lock end time (permanent lock if empty)")
  private LocalDateTime lockEndDate;

}

