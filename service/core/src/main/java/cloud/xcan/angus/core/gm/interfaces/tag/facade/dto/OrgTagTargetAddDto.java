package cloud.xcan.angus.core.gm.interfaces.tag.facade.dto;

import cloud.xcan.angus.api.commonlink.tag.OrgTargetType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class OrgTagTargetAddDto implements Serializable {

  @NotNull
  @Schema(description = "Tag organization type", requiredMode = RequiredMode.REQUIRED)
  private OrgTargetType targetType;

  @NotNull
  @Schema(description = "Tag organization id", example = "1", requiredMode = RequiredMode.REQUIRED)
  private Long targetId;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OrgTagTargetAddDto that = (OrgTagTargetAddDto) o;
    return targetType == that.targetType &&
        targetId.equals(that.targetId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(targetType, targetId);
  }
}
