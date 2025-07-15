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

  @Schema(description = "Authorization policy id. The ID is required when modifying an existing "
      + "policy, create a new policy when the value is empty", example = "1")
  private Long id;

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Authorization policy name", example = "System administrator",
      maxLength = MAX_NAME_LENGTH, requiredMode = RequiredMode.REQUIRED)
  private String name;

  @Code
  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "Authorization policy code. Note: Modification not allowed",
      example = "admin", maxLength = MAX_CODE_LENGTH)
  private String code;

  @Schema(description = "Authorization policy type. Note: Modification not allowed")
  private PolicyType type;

  @NotNull
  @Schema(description = "Default policy flag", defaultValue = "false", example = "false",
      requiredMode = RequiredMode.REQUIRED)
  private Boolean default0;

  //Modification not allowed
  //@Schema(description = "Policy initialization authorization, modification not allowed")
  //private PolicyGrantStage grantStage;

  @Length(max = MAX_DESC_LENGTH)
  @Schema(description = "Authorization policy description", example = "System management policy",
      maxLength = MAX_DESC_LENGTH)
  private String description;

  @Schema(description = "The application id of authorization policy. Note: Modification not allowed", example = "1")
  private Long appId;

  @Size(max = MAX_POLICY_FUNC_NUM)
  @Schema(description = "The application function ids of authorization policy, the maximum support is `2000`")
  private Set<Long> funcIds;

  @JsonIgnore
  public boolean isDefaultType() {
    return nonNull(type) && (type.equals(PolicyType.PRE_DEFINED)
        || type.equals(PolicyType.USER_DEFINED));
  }
}
