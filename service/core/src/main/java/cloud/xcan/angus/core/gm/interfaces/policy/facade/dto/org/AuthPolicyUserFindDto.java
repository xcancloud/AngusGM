package cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.org;

import cloud.xcan.angus.api.enums.UserSource;
import cloud.xcan.angus.remote.PageQuery;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class AuthPolicyUserFindDto extends PageQuery {

  @JsonIgnore
  @Schema(hidden = true)
  private Long policyId;

  @Schema(description = "User identifier")
  private Long id;

  @Schema(description = "User login username")
  private String username;

  @Schema(description = "User full name")
  private String fullName;

  @Schema(description = "User mobile phone number")
  private String mobile;

  @Schema(description = "User email address")
  private String email;

  @Schema(description = "User source or origin")
  private UserSource source;

  @Schema(description = "Whether the user account is enabled and active")
  private Boolean enabled;

  @Schema(description = "Whether the user has system administrator privileges")
  private Boolean sysAdmin;

  @Schema(description = "User creator identifier")
  private Long createdBy;

  @Schema(description = "User creation timestamp")
  private LocalDateTime createdDate;

}
