package cloud.xcan.angus.core.gm.interfaces.policy.facade.dto;

import static cloud.xcan.angus.api.commonlink.AuthConstant.MAX_POLICY_FUNC_NUM;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_DESC_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
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
public class AuthPolicyUpdateDto {

  @NotNull
  @Schema(description = "Authorization policy id", example = "1", requiredMode = RequiredMode.REQUIRED)
  private Long id;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Authorization policy name", example = "System administrator", maxLength = MAX_NAME_LENGTH)
  private String name;

  //private String code;

  //private PolicyType type;

  @Schema(description = "Default authorization policy flag", defaultValue = "false", example = "false")
  private Boolean default0;

  //private PolicyGrantStage grantStage;

  @Length(max = MAX_DESC_LENGTH)
  @Schema(description = "Authorization policy description", example = "System management policy")
  private String description;

  //private Long appId;

  @Size(max = MAX_POLICY_FUNC_NUM)
  @Schema(description = "The application function ids of authorization policy, the maximum support is `2000`")
  private Set<Long> funcIds;

}
