package cloud.xcan.angus.core.gm.interfaces.policy.facade.dto;

import static cloud.xcan.angus.api.commonlink.AuthConstant.MAX_POLICY_FUNC_NUM;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_DESC_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;

import cloud.xcan.angus.api.commonlink.policy.PolicyType;
import cloud.xcan.angus.validator.Code;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Accessors(chain = true)
public class AuthPolicyAddDto {

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Display name for the authorization policy", example = "System Administrator Policy",
      requiredMode = RequiredMode.REQUIRED)
  private String name;

  @NotBlank
  @Code
  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "Unique identifier code for the authorization policy", example = "SYS_ADMIN",
      requiredMode = RequiredMode.REQUIRED)
  private String code;

  @NotNull
  @Schema(description = "Classification type of the authorization policy", requiredMode = RequiredMode.REQUIRED)
  private PolicyType type;

  @NotNull
  @Schema(description = "Whether this policy is the default authorization policy", defaultValue = "false", example = "false",
      requiredMode = RequiredMode.REQUIRED)
  private Boolean default0;

  //@NotNull
  //@Schema(description = "Policy initialization authorization, default NONE - Do not initialize automatically", requiredMode = RequiredMode.REQUIRED)
  //private PolicyGrantStage grantStage;

  @Length(max = MAX_DESC_LENGTH)
  @Schema(description = "Detailed description of the authorization policy functionality", example = "Comprehensive system administration policy with full access privileges")
  private String description;

  @NotNull
  @Schema(description = "Application identifier that this authorization policy belongs to", requiredMode = RequiredMode.REQUIRED)
  private Long appId;

  @Size(max = MAX_POLICY_FUNC_NUM)
  @Schema(description = "Application function identifiers associated with this authorization policy. Maximum supported: 2000")
  private Set<Long> funcIds;

}
