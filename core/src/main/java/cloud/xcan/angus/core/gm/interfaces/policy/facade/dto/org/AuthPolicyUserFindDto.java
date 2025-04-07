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

}
