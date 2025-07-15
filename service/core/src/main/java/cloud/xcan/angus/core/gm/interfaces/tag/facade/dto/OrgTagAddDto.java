package cloud.xcan.angus.core.gm.interfaces.tag.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;


@Getter
@Setter
public class OrgTagAddDto implements Serializable {

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Organization tag name", example = "developer", maxLength = MAX_NAME_LENGTH,
      requiredMode = RequiredMode.REQUIRED)
  private String name;

}
