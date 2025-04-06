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
  @Schema(description = "User id.", example = "1", requiredMode = RequiredMode.REQUIRED)
  private Long userId;

  @NotNull
  @Schema(description = "Whether or not the department head flag.", example = "true",
      requiredMode = RequiredMode.REQUIRED)
  public Boolean head;

}
