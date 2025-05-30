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

  private Long id;

  private String name;

  private String code;

  private Boolean enabled;

  private Long createdBy;

  private LocalDateTime createdDate;

}
