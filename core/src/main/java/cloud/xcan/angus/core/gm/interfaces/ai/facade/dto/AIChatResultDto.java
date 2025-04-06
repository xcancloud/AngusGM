package cloud.xcan.angus.core.gm.interfaces.ai.facade.dto;

import cloud.xcan.angus.core.gm.infra.ai.AIChatType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class AIChatResultDto {

  @NotNull
  @Schema(description = "Chat business type", requiredMode = RequiredMode.REQUIRED)
  private AIChatType type;

  @NotEmpty
  @Schema(description = "Chat business question", requiredMode = RequiredMode.REQUIRED)
  private String question;

}
