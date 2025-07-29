package cloud.xcan.angus.core.gm.interfaces.dept.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class DeptHeadReplaceDto implements Serializable {

  @NotNull
  @Schema(description = "User identifier to assign as department head", requiredMode = RequiredMode.REQUIRED)
  private Long userId;

  @NotNull
  @Schema(description = "Whether to assign user as department head", example = "true", requiredMode = RequiredMode.REQUIRED)
  public Boolean head;

}
