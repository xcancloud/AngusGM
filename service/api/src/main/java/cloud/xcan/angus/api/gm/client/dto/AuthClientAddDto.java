package cloud.xcan.angus.api.gm.client.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CLIENT_SECRET_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_DESC_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_KEY_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_KEY_LENGTH_X2;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH_X2;

import cloud.xcan.angus.api.commonlink.client.ClientSource;
import cloud.xcan.angus.api.enums.Platform;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
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
public class AuthClientAddDto {

  /**
   * OAuth2 client fields
   */
  @Length(max = MAX_KEY_LENGTH)
  @Schema(description = "Client primary key ID for OAuth2 registration. Automatically generates UUID string if not specified. Used for unique client identification",
      maxLength = MAX_KEY_LENGTH)
  private String id;

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description =
      "Unique client identifier for OAuth2 server authentication. Enables authorization and token requests for the application. Must be unique across the system",
      maxLength = MAX_NAME_LENGTH, requiredMode = RequiredMode.REQUIRED)
  private String clientId;

  //@Schema(description = "The time at which the client identifier was issued")
  //private LocalDateTime clientIdIssuedAt;

  @Length(max = MAX_CLIENT_SECRET_LENGTH)
  @Schema(description =
      "Client secret for secure application authentication. Ensures only trusted clients access protected resources. Can be null if not available",
      maxLength = MAX_CLIENT_SECRET_LENGTH)
  private String clientSecret;

  @Schema(description = "Timestamp when the client secret expires. Null if the secret does not expire. Used for secret lifecycle management")
  private LocalDateTime clientSecretExpiresAt;

  @NotBlank
  @Length(max = MAX_NAME_LENGTH_X2)
  @Schema(description = "Human-readable name for the OAuth2 client. Used for display and identification purposes in management interfaces",
      maxLength = MAX_NAME_LENGTH_X2, requiredMode = RequiredMode.REQUIRED)
  private String clientName;

  @NotEmpty
  @Schema(description = "Set of authentication methods that the client may use. Required for proper client authentication configuration", requiredMode = RequiredMode.REQUIRED)
  private Set<String> clientAuthenticationMethods;

  @NotEmpty
  @Schema(description = "Set of authorization grant types that the client may use. Required for proper authorization flow configuration", requiredMode = RequiredMode.REQUIRED)
  private Set<String> authorizationGrantTypes;

  @Schema(description = "Set of redirect URIs that the client may use in redirect-based flows. Used for authorization code and implicit grant flows")
  private Set<String> redirectUris;

  @Schema(description = "Set of post-logout redirect URIs for client logout flows. Used when requesting End-User's User Agent redirection after logout")
  private Set<String> postLogoutRedirectUris;

  @NotEmpty
  @Schema(description = "Set of scopes that the client may use. Required for proper scope-based access control and authorization", requiredMode = RequiredMode.REQUIRED)
  private Set<String> scopes;

  @NotEmpty
  @Schema(description = "Map of client configuration settings. Required for proper client behavior configuration and customization", requiredMode = RequiredMode.REQUIRED)
  private Map<String, Object> clientSettings;

  @NotEmpty
  @Schema(description = "Map of token configuration settings. Required for proper token behavior configuration and customization", requiredMode = RequiredMode.REQUIRED)
  private Map<String, Object> tokenSettings;

  // OAuth2 client temp fields <- Important:: Use scopes instead of authorization authorities permission.
  //@Size(max = MAX_CLIENT_AUTHORITY_SIZE)
  //@Schema(description = "The authorities granted to the client")
  //private List<String> authorities;

  /**
   * AngusGM Client Info.
   */
  @Length(max = MAX_DESC_LENGTH)
  @Schema(description = "Human-readable description of the client. Used for documentation and management purposes", maxLength = MAX_NAME_LENGTH_X2)
  private String description;

  @Schema(description = "Boolean flag indicating whether the client is enabled or disabled. Defaults to true for new clients", defaultValue = "true")
  private Boolean enabled;

  @Schema(description = "Platform used by the client for proper platform-specific configuration. Defaults to XCAN_TP for new clients", defaultValue = "XCAN_TP")
  private Platform platform;

  @Schema(description = "Source of client registration for tracking and management. Defaults to XCAN_USER_TOKEN for new clients", defaultValue = "XCAN_USER_TOKEN")
  private ClientSource source;

  @Length(max = MAX_KEY_LENGTH_X2)
  @Schema(description = "Business tag for client categorization and management. Used for organizational purposes", maxLength = MAX_KEY_LENGTH_X2)
  private String bizTag;

  @Schema(description = "Tenant ID for which the client is registered. Empty value indicates effective for all tenants")
  private String tenantId;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Human-readable tenant name associated with the client. Used for display and identification purposes", maxLength = MAX_NAME_LENGTH)
  private String tenantName;

}
