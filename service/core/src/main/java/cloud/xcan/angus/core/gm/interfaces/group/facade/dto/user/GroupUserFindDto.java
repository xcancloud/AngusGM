package cloud.xcan.angus.core.gm.interfaces.group.facade.dto.user;

import cloud.xcan.angus.remote.PageQuery;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class GroupUserFindDto extends PageQuery {

  @JsonIgnore
  @Schema(hidden = true)
  private Long groupId;

  @Schema(description = "User identifier for filtering")
  private Long userId;

  @Schema(description = "User full name for filtering")
  private String fullName;

}
