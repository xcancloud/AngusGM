package cloud.xcan.angus.api.gm.user.to;

import cloud.xcan.angus.remote.NameJoinField;
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
public class UserDeptTo implements Serializable {

  @NotNull
  @Schema(description = "User department id.", requiredMode = RequiredMode.REQUIRED)
  private Long id;

  @NameJoinField(id = "id", repository = "deptRepo")
  @Schema(description = "User department name.")
  private String name;

  @Schema(description = "Whether or not main department flag.")
  private Boolean mainDept;

  @Schema(description = "Whether or not department head flag.")
  private Boolean deptHead;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserDeptTo that = (UserDeptTo) o;
    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
