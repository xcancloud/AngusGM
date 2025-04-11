package cloud.xcan.angus.api.gm.user.to;

import cloud.xcan.angus.remote.NameJoinField;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class UserGroupTo implements Serializable {

  @NotNull
  @Schema(description = "User group id.", requiredMode = RequiredMode.REQUIRED)
  private Long id;

  @NameJoinField(id = "id", repository = "groupRepo")
  @Schema(description = "User group name.")
  private String name;

}
