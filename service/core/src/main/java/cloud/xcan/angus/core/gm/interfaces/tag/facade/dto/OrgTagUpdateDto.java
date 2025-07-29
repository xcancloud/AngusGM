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
  @Schema(description = "Organizational tag unique identifier", example = "1", requiredMode = RequiredMode.REQUIRED)
  private Long id;

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Organizational tag display name for categorization", example = "dev", requiredMode = RequiredMode.REQUIRED)
  private String name;

}
