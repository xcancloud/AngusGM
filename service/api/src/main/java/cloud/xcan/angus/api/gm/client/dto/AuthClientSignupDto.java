package cloud.xcan.angus.api.gm.client.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;

import cloud.xcan.angus.api.commonlink.client.Client2pSignupBiz;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Setter
@Getter
@Accessors(chain = true)
public class AuthClientSignupDto {

  @NotNull
  @Schema(description = "Privatization client type for signup configuration. Required for proper client registration and setup", requiredMode = RequiredMode.REQUIRED)
  private Client2pSignupBiz signupBiz;

  @NotNull
  @Schema(description = "Grant tenant ID for client registration. Required for proper tenant association and access control", requiredMode = RequiredMode.REQUIRED)
  private Long tenantId;

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Grant tenant name for client registration. Used for display and identification purposes", requiredMode = RequiredMode.REQUIRED)
  private String tenantName;

  @NotNull
  @Schema(description = "Grant resource ID for client registration. Required for proper resource association and access control", requiredMode = RequiredMode.REQUIRED)
  private Long resourceId;

}
