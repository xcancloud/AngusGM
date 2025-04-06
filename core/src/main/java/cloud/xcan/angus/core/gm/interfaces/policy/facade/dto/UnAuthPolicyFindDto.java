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
public class UnAuthPolicyFindDto extends PageQuery {

  private Long id;

  private String name;

  private String code;

  private PolicyType type;

  private Boolean default0;

  private Boolean enabled;

  @JsonIgnore
  @Schema(description = "Whether or not ignore the authorization organization information flag.", defaultValue = "false", hidden = true)
  private transient Boolean ignoreAuthOrg = true;

  private PolicyGrantStage grantStage;

  private String description;

  private Long appId;

  private String clientId;

  private LocalDateTime createdDate;

  @JsonIgnore
  @Schema(hidden = true)
  private Long orgId;

  @JsonIgnore
  @Schema(hidden = true)
  private AuthOrgType orgType;

}
