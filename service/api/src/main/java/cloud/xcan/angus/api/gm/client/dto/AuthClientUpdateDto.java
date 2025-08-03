package cloud.xcan.angus.api.gm.client.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CLIENT_SECRET_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_DESC_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_KEY_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_KEY_LENGTH_X2;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH_X2;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Setter
@Getter
@Accessors(chain = true)
public class AuthClientUpdateDto {

  /**
   * OAuth2 client fields
   */
  @NotEmpty
  @Length(max = MAX_KEY_LENGTH)
  @Schema(description = "Client primary key ID for OAuth2 update. Required for identifying the client to be updated", requiredMode = RequiredMode.REQUIRED)
  private String id;

  @Length(max = MAX_CLIENT_SECRET_LENGTH)
  @Schema(description =  "Client secret for secure application authentication. Ensures only trusted clients access protected resources. Can be null if not available")
  private String clientSecret;

  @Schema(description = "Timestamp when the client secret expires. Null if the secret does not expire. Used for secret lifecycle management")
  private LocalDateTime clientSecretExpiresAt;

  @Length(max = MAX_NAME_LENGTH_X2)
  @Schema(description = "Human-readable name for the OAuth2 client. Used for display and identification purposes in management interfaces")
  private String clientName;

  @Schema(description = "Set of authentication methods that the client may use. Used for proper client authentication configuration")
  private Set<String> clientAuthenticationMethods;

  @Schema(description = "Set of authorization grant types that the client may use. Used for proper authorization flow configuration")
  private Set<String> authorizationGrantTypes;

  @Schema(description = "Set of redirect URIs that the client may use in redirect-based flows. Used for authorization code and implicit grant flows")
  private Set<String> redirectUris;

  @Schema(description = "Set of post-logout redirect URIs for client logout flows. Used when requesting End-User's User Agent redirection after logout")
  private Set<String> postLogoutRedirectUris;

  @Schema(description = "Set of scopes that the client may use. Used for proper scope-based access control and authorization")
  private Set<String> scopes;

  @Schema(description = "Map of client configuration settings. Used for proper client behavior configuration and customization")
  private Map<String, Object> clientSettings;

  @Schema(description = "Map of token configuration settings. Used for proper token behavior configuration and customization")
  private Map<String, Object> tokenSettings;

  /**
   * AngusGM Client Info.
   */
  @Length(max = MAX_DESC_LENGTH)
  @Schema(description = "Human-readable description of the client. Used for documentation and management purposes")
  private String description;

  @Schema(description = "Boolean flag indicating whether the client is enabled or disabled. Used for client lifecycle management")
  private Boolean enabled;

  @Length(max = MAX_KEY_LENGTH_X2)
  @Schema(description = "Business tag for client categorization and management. Used for organizational purposes")
  private String bizTag;

}
