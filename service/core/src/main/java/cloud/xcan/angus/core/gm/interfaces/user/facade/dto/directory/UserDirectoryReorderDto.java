package cloud.xcan.angus.core.gm.interfaces.user.facade.dto.directory;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class UserDirectoryReorderDto implements Serializable {

  @NotNull
  @Schema(description = "Directory id", requiredMode = RequiredMode.REQUIRED)
  private Long id;

  @NotNull
  @Schema(description = "Sorting value, the synchronization priority is high if the value is small", requiredMode = RequiredMode.REQUIRED)
  private Integer sequence;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserDirectoryReorderDto that = (UserDirectoryReorderDto) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
