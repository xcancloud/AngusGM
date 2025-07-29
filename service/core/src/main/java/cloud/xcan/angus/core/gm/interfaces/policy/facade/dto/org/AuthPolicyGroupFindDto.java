package cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.org;

import cloud.xcan.angus.remote.PageQuery;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class AuthPolicyGroupFindDto extends PageQuery {

  @JsonIgnore
  @Schema(hidden = true)
  private Long policyId;

  @Schema(description = "Group identifier")
  private Long id;

  @Schema(description = "Group display name")
  private String name;

  @Schema(description = "Group unique code")
  private String code;

  @Schema(description = "Whether the group is enabled and active")
  private Boolean enabled;

  @Schema(description = "Group creator identifier")
  private Long createdBy;

  @Schema(description = "Group creation timestamp")
  private LocalDateTime createdDate;

}
