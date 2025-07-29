package cloud.xcan.angus.core.gm.interfaces.to.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_DESC_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;

import cloud.xcan.angus.validator.Code;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;


@Getter
@Setter
@Accessors(chain = true)
public class TORoleReplaceDto {

  @Schema(description = "Operational role identifier (required for updates, empty for new role creation)")
  private Long id;

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Operational role display name", example = "System administrator",
      requiredMode = RequiredMode.REQUIRED)
  private String name;

  @Code
  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "Unique operational role identifier code (cannot be modified)", example = "TOP_TENANT_ADMIN")
  private String code;

  @Length(max = MAX_DESC_LENGTH)
  @Schema(description = "Operational role functional description", example = "System management policy")
  private String description;

  @NotNull
  @Schema(description = "Associated application identifier", requiredMode = RequiredMode.REQUIRED)
  private Long appId;

}
