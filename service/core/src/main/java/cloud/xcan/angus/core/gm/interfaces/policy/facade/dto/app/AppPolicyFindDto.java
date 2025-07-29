package cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.app;

import cloud.xcan.angus.api.commonlink.policy.PolicyGrantScope;
import cloud.xcan.angus.api.commonlink.policy.PolicyGrantStage;
import cloud.xcan.angus.api.commonlink.policy.PolicyType;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AppPolicyFindDto extends PageQuery {

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

  @Schema(description = "Authorization policy grant scope")
  private PolicyGrantScope grantScope;

  @Schema(description = "Authorization policy grant stage")
  private PolicyGrantStage grantStage;

  @Schema(description = "Authorization policy detailed description")
  private String description;

  @Schema(description = "Application identifier that this policy belongs to")
  private Long appId;

  @Schema(description = "Policy creation timestamp")
  private LocalDateTime createdDate;

}
