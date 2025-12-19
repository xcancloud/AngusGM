package cloud.xcan.angus.core.gm.interfaces.user.facade.dto.group;


import cloud.xcan.angus.remote.PageQuery;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class UserGroupFindDto extends PageQuery {

  @JsonIgnore
  @Schema(hidden = true)
  private Long userId;

  @Schema(description = "Group identifier for filtering user-group relationships")
  private Long groupId;

  @Schema(description = "Group name for searching user-group relationships")
  private String groupName;

  @Override
  public String getDefaultOrderBy() {
    return "createdDate";
  }

}
