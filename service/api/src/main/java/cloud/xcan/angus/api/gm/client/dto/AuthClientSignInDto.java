package cloud.xcan.angus.api.gm.client.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CLIENT_SECRET_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Setter
@Getter
@Accessors(chain = true)
public class AuthClientSignInDto {

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description =
      "Unique client identifier for OAuth2 server authentication. Enables authorization and token requests for the application. Must be unique across the system",
      maxLength = MAX_NAME_LENGTH, requiredMode = RequiredMode.REQUIRED)
  private String clientId;

  @NotBlank
  @Length(max = MAX_CLIENT_SECRET_LENGTH)
  @Schema(description =
      "Client secret for secure application authentication. Ensures only trusted clients access protected resources. Required for client authentication",
      maxLength = MAX_CLIENT_SECRET_LENGTH, requiredMode = RequiredMode.REQUIRED)
  private String clientSecret;

  @NotEmpty
  @Schema(description = "Scope that the client may use for authorization. Required for proper scope-based access control", requiredMode = RequiredMode.REQUIRED)
  private String scope;

}
