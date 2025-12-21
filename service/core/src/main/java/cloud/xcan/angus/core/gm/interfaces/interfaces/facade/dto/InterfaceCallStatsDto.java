package cloud.xcan.angus.core.gm.interfaces.interfaces.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Interface call stats query DTO
 */
@Getter
@Setter
@Accessors(chain = true)
public class InterfaceCallStatsDto implements Serializable {

  @Schema(description = "Start date")
  private LocalDate startDate;

  @Schema(description = "End date")
  private LocalDate endDate;
}
