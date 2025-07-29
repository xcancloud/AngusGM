package cloud.xcan.angus.core.gm.interfaces.policy.facade.dto;

import static cloud.xcan.angus.api.commonlink.AuthConstant.MAX_POLICY_FUNC_NUM;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_DESC_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.policy.PolicyType;
import cloud.xcan.angus.validator.Code;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class AuthPolicyReplaceDto {

  @Schema(description = "Authorization policy identifier for updating existing policy. Leave empty to create new policy", example = "1")
  private Long id;

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Display name for the authorization policy", example = "System Administrator Policy", requiredMode = RequiredMode.REQUIRED)
  private String name;

  @Code
  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "Unique identifier code for the authorization policy. Cannot be modified after creation", example = "SYS_ADMIN")
  private String code;

  @Schema(description = "Classification type of the authorization policy. Cannot be modified after creation")
  private PolicyType type;

  @NotNull
  @Schema(description = "Whether this policy is the default authorization policy", defaultValue = "false", example = "false", requiredMode = RequiredMode.REQUIRED)
  private Boolean default0;

  //Modification not allowed
  //@Schema(description = "Policy initialization authorization, modification not allowed")
  //private PolicyGrantStage grantStage;

  @Length(max = MAX_DESC_LENGTH)
  @Schema(description = "Detailed description of the authorization policy functionality", example = "Comprehensive system administration policy with full access privileges")
  private String description;

  @Schema(description = "Application identifier that this authorization policy belongs to. Cannot be modified after creation", example = "1")
  private Long appId;

  @Size(max = MAX_POLICY_FUNC_NUM)
  @Schema(description = "Application function identifiers associated with this authorization policy")
  private Set<Long> funcIds;

  @JsonIgnore
  public boolean isDefaultType() {
    return nonNull(type) && (type.equals(PolicyType.PRE_DEFINED)
        || type.equals(PolicyType.USER_DEFINED));
  }
}
