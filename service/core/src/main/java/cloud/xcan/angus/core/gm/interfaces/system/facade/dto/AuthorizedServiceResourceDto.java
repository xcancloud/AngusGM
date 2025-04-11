package cloud.xcan.angus.core.gm.interfaces.system.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;


@Getter
@Setter
@Accessors(chain = true)
public class AuthorizedServiceResourceDto {

  @NotBlank
  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "Authorized service code.", requiredMode = RequiredMode.REQUIRED)
  private String serviceCode;

  @NotEmpty
  @Schema(description = "Authorized resources.", requiredMode = RequiredMode.REQUIRED)
  private List<AuthorizedResourceDto> resources;

}
