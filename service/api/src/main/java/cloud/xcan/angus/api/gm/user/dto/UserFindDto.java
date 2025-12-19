package cloud.xcan.angus.api.gm.user.dto;

import cloud.xcan.angus.api.enums.UserSource;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class UserFindDto extends PageQuery {

  @Schema(description = "User identifier for precise user lookup. Used for specific user identification and retrieval")
  private Long id;

  @Schema(description = "Username for search and filtering. Used for user identification and authentication")
  private String username;

  @Schema(description = "User full name for search and filtering. Used for user identification and display")
  private String fullName;

  @Schema(description = "User mobile number for contact filtering. Used for user contact information and verification")
  private String mobile;

  @Schema(description = "User email address for contact filtering. Used for user contact information and verification")
  private String email;

  @Schema(description = "User source for registration filtering. Used for identifying the registration channel and source")
  private UserSource source;

  @Schema(description = "User enabled status for account filtering. Used for account state management and access control")
  private Boolean enabled;

  @Schema(description = "User system administrator flag for role filtering. Used for administrative access and permissions")
  private Boolean sysAdmin;

  @Schema(description = "User tag identifier for categorization filtering. Used for user organization and grouping")
  private Long tagId;

  @Override
  public String getDefaultOrderBy() {
    return "createdDate";
  }

}
