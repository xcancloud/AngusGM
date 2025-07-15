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
public class AuthClientReplaceDto {

  /**
   * OAuth2 client fields
   */
  @Length(max = MAX_KEY_LENGTH)
  @Schema(description =
      "Client primary key id. The ID is required when modifying an existing client, "
          + "create a new client when the value is empty.",
      maxLength = MAX_KEY_LENGTH)
  private String id;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description =
      "OAuth2 registered client identifier. The clientId uniquely identifies an application "
          + "to the OAuth2 server, enabling authorization and token requests. Note: The modification is not allowed.",
      maxLength = MAX_NAME_LENGTH)
  private String clientId;

  //@Schema(description = "The time at which the client identifier was issued.")
  //private LocalDateTime clientIdIssuedAt;

  @Length(max = MAX_CLIENT_SECRET_LENGTH)
  @Schema(description =
      "OAuth2 registered client secret or null if not available. The client secret securely "
          + "authenticates the application's identity, ensuring only trusted clients access protected resources.",
      maxLength = MAX_CLIENT_SECRET_LENGTH)
  private String clientSecret;

  @Schema(description = "The time at which the client secret expires or null if it does not expire..")
  private LocalDateTime clientSecretExpiresAt;

  @NotBlank
  @Length(max = MAX_NAME_LENGTH_X2)
  @Schema(description = "OAuth2 registered client name.", maxLength = MAX_NAME_LENGTH_X2,
      requiredMode = RequiredMode.REQUIRED)
  private String clientName;

  @NotEmpty
  @Schema(description = "The authentication method(s) that the client may use.", requiredMode = RequiredMode.REQUIRED)
  private Set<String> clientAuthenticationMethods;

  @NotEmpty
  @Schema(description = "The authorization grant type(s) that the client may use.", requiredMode = RequiredMode.REQUIRED)
  private Set<String> authorizationGrantTypes;

  @Schema(description = "The redirect URI(s) that the client may use in redirect-based flows.")
  private Set<String> redirectUris;

  @Schema(description = "The post logout redirect URI(s) that the client may use for logout. "
      + "The post_logout_redirect_uri parameter is used by the client when requesting that the End-User's "
      + "User Agent be redirected to after a logout has been performed.")
  private Set<String> postLogoutRedirectUris;

  @NotEmpty
  @Schema(description = "The scope(s) that the client may use.", requiredMode = RequiredMode.REQUIRED)
  private Set<String> scopes;

  @NotEmpty
  @Schema(description = "The client configuration settings.", requiredMode = RequiredMode.REQUIRED)
  private Map<String, Object> clientSettings;

  @NotEmpty
  @Schema(description = "The token configuration settings.", requiredMode = RequiredMode.REQUIRED)
  private Map<String, Object> tokenSettings;

  // OAuth2 client temp fields <- Important:: Use scopes instead of authorization authorities permission.
  //@Size(max = MAX_CLIENT_AUTHORITY_SIZE)
  //@Schema(description = "The authorities granted to the client.")
  //private List<String> authorities;

  /**
   * AngusGM Client Info.
   */
  @Length(max = MAX_DESC_LENGTH)
  @Schema(description = "Client description.", maxLength = MAX_NAME_LENGTH_X2)
  private String description;

  @Schema(description = "Client enabled or disabled status.", defaultValue = "true")
  private Boolean enabled;

  @Schema(description = "The platform used by the client. Note: The modification is not allowed.", defaultValue = "XCAN_TP")
  private Platform platform;

  @Schema(description = "The registered source of client. Note: The modification is not allowed.", defaultValue = "XCAN_USER_TOKEN")
  private ClientSource source;

  @Length(max = MAX_KEY_LENGTH_X2)
  @Schema(description = "The business tag of client.", maxLength = MAX_KEY_LENGTH_X2)
  private String bizTag;

  @Schema(description =
      "The client used by the tenant (id), effective for all tenants when the value is empty. "
          + "Note: The modification is not allowed.")
  private String tenantId;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "The client used by the tenant (name). Note: The modification is not allowed.",
      maxLength = MAX_NAME_LENGTH)
  private String tenantName;

}
