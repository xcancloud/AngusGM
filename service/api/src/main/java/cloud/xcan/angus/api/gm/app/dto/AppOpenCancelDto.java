package cloud.xcan.angus.api.gm.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class AppOpenCancelDto {

  @NotNull
  @Schema(description = "Required application ID to identify which application to cancel", requiredMode = RequiredMode.REQUIRED)
  private Long appId;

}
