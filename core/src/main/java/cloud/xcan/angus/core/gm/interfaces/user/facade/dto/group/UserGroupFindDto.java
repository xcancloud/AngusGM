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

  private Long groupId;

  private String groupName;

  private Long createdBy;

  private LocalDateTime createdDate;

  @Override
  public String getDefaultOrderBy() {
    return "createdDate";
  }

}
