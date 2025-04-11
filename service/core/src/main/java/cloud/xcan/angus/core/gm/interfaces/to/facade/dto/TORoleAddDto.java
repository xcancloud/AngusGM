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
public class TORoleAddDto {

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Operation role name.", example = "System administrator",
      maxLength = MAX_NAME_LENGTH, requiredMode = RequiredMode.REQUIRED)
  private String name;

  @Code
  @NotBlank
  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "Operation role code.", example = "TOP_TENANT_ADMIN",
      maxLength = MAX_CODE_LENGTH, requiredMode = RequiredMode.REQUIRED)
  private String code;

  @Length(max = MAX_DESC_LENGTH)
  @Schema(description = "Operation role description.", maxLength = MAX_CODE_LENGTH,
      example = "System management policy")
  private String description;

  @NotNull
  @Schema(description = "Operation application id.", example = "1",
      requiredMode = RequiredMode.REQUIRED)
  private Long appId;

}
