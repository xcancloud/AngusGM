package cloud.xcan.angus.core.gm.interfaces.policy.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AuthPolicyInitDto {

  @NotNull
  @Schema(description = "Authorization policy identifier to initialize", example = "1", requiredMode = RequiredMode.REQUIRED)
  private Long id;

}
