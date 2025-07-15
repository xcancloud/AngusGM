package cloud.xcan.angus.core.gm.interfaces.tag.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;


@Setter
@Getter
@Accessors(chain = true)
public class OrgTagUpdateDto implements Serializable {

  @NotNull
  @Schema(description = "Organization tag id", example = "1", requiredMode = RequiredMode.REQUIRED)
  private Long id;

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Organization tag name", example = "dev", maxLength = MAX_NAME_LENGTH,
      requiredMode = RequiredMode.REQUIRED)
  private String name;

}
