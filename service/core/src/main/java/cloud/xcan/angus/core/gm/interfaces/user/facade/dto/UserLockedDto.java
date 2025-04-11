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
  @Schema(description = "User id.", example = "1", requiredMode = RequiredMode.REQUIRED)
  private Long id;

  @NotNull
  @Schema(description = "Whether or not lock user flag.", requiredMode = RequiredMode.REQUIRED)
  private Boolean locked;

  @Schema(description = "Start lock time.")
  private LocalDateTime lockStartDate;

  @Schema(description = "DistributedLock end time, permanently locked when value is empty.")
  private LocalDateTime lockEndDate;

}

