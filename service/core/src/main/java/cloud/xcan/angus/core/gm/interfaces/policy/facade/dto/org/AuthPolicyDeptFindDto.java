package cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.org;

import cloud.xcan.angus.remote.PageQuery;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class AuthPolicyDeptFindDto extends PageQuery {

  @JsonIgnore
  @Schema(hidden = true)
  private Long policyId;

  @Schema(description = "Department identifier")
  private Long id;

  @Schema(description = "Department unique code")
  private String code;

  @Schema(description = "Department display name")
  private String name;

  @Schema(description = "Parent department identifier")
  private Long pid;

  @Schema(description = "Department hierarchy level")
  private Integer level;

  @Schema(description = "Department creator identifier")
  private Long createdBy;

  @Schema(description = "Department creation timestamp")
  private LocalDateTime createdDate;

}
