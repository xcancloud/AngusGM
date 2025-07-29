package cloud.xcan.angus.core.gm.interfaces.to.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_DESC_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;


@Getter
@Setter
@Accessors(chain = true)
public class TORoleUpdateDto {

  @NotNull
  @Schema(description = "Operational role unique identifier", example = "1", requiredMode = RequiredMode.REQUIRED)
  private Long id;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Operational role display name", example = "System administrator")
  private String name;

  @Length(max = MAX_DESC_LENGTH)
  @Schema(description = "Operational role functional description", example = "System management policy")
  private String description;

}
