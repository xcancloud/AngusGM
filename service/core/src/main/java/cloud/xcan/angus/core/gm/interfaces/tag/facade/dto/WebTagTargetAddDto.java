package cloud.xcan.angus.core.gm.interfaces.tag.facade.dto;

import cloud.xcan.angus.api.commonlink.app.tag.WebTagTargetType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class WebTagTargetAddDto {

  @NotNull
  @Schema(description = "Web application resource type.", requiredMode = RequiredMode.REQUIRED)
  private WebTagTargetType targetType;

  @NotNull
  @Schema(description = "Web application resource id.", example = "1", requiredMode = RequiredMode.REQUIRED)
  private Long targetId;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WebTagTargetAddDto that = (WebTagTargetAddDto) o;
    return targetType == that.targetType && targetId.equals(that.targetId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(targetType, targetId);
  }
}
