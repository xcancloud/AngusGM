package cloud.xcan.angus.core.gm.interfaces.to.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class TOUserAddDto {

  @NotNull
  @Schema(description = "Operational user id.", example = "1", requiredMode = RequiredMode.REQUIRED)
  private Long id;

}
