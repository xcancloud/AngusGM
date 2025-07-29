package cloud.xcan.angus.core.gm.interfaces.policy.facade.dto;

import cloud.xcan.angus.api.commonlink.AuthOrgType;
import cloud.xcan.angus.api.commonlink.policy.PolicyGrantStage;
import cloud.xcan.angus.api.commonlink.policy.PolicyType;
import cloud.xcan.angus.remote.PageQuery;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class UnAuthPolicyAssociatedFindDto extends PageQuery {

  @Schema(description = "Authorization policy identifier")
  private Long id;

  @Schema(description = "Authorization policy display name")
  private String name;

  @Schema(description = "Authorization policy unique code")
  private String code;

  @Schema(description = "Authorization policy type classification")
  private PolicyType type;

  @Schema(description = "Whether this is the default authorization policy")
  private Boolean default0;

  @Schema(description = "Whether the authorization policy is enabled and active")
  private Boolean enabled;

  @JsonIgnore
  @Schema(hidden = true)
  private Boolean ignoreAuthOrg = true;

  @JsonIgnore
  @Schema(hidden = true)
  private Boolean adminFullAssociated = true;

  @Schema(description = "Authorization policy grant stage")
  private PolicyGrantStage grantStage;

  @Schema(description = "Authorization policy detailed description")
  private String description;

  @Schema(description = "Application identifier that this policy belongs to")
  private Long appId;

  @Schema(description = "Client application identifier")
  private String clientId;

  @Schema(description = "Policy creation timestamp")
  private LocalDateTime createdDate;

  @JsonIgnore
  @Schema(hidden = true)
  private Long orgId;

  @JsonIgnore
  @Schema(hidden = true)
  private AuthOrgType orgType;

}
