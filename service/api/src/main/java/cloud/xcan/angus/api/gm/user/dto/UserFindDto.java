package cloud.xcan.angus.api.gm.user.dto;

import cloud.xcan.angus.api.enums.UserSource;
import cloud.xcan.angus.remote.PageQuery;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class UserFindDto extends PageQuery {

  private Long id;

  private String username;

  private String fullName;

  private String mobile;

  private String email;

  private UserSource source;

  private Boolean enabled;

  private Boolean sysAdmin;

  private Long createdBy;

  private LocalDateTime createdDate;

  private Long tagId;

  @Override
  public String getDefaultOrderBy() {
    return "createdDate";
  }

}
