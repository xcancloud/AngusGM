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
  @Schema(description = "Client primary key id. ", maxLength = MAX_KEY_LENGTH, requiredMode = RequiredMode.REQUIRED)
  private String id;

  @Length(max = MAX_CLIENT_SECRET_LENGTH)
  @Schema(description =
      "OAuth2 registered client secret or null if not available. The client secret securely "
          + "authenticates the application's identity, ensuring only trusted clients access protected resources.",
      maxLength = MAX_CLIENT_SECRET_LENGTH)
  private String clientSecret;

  @Schema(description = "The time at which the client secret expires or null if it does not expire..")
  private LocalDateTime clientSecretExpiresAt;

  @Length(max = MAX_NAME_LENGTH_X2)
  @Schema(description = "OAuth2 registered client name.", maxLength = MAX_NAME_LENGTH_X2)
  private String clientName;

  @Schema(description = "The authentication method(s) that the client may use.")
  private Set<String> clientAuthenticationMethods;

  @Schema(description = "The authorization grant type(s) that the client may use.")
  private Set<String> authorizationGrantTypes;

  @Schema(description = "The redirect URI(s) that the client may use in redirect-based flows.")
  private Set<String> redirectUris;

  @Schema(description = "The post logout redirect URI(s) that the client may use for logout. "
      + "The post_logout_redirect_uri parameter is used by the client when requesting that the End-User's "
      + "User Agent be redirected to after a logout has been performed.")
  private Set<String> postLogoutRedirectUris;

  @Schema(description = "The scope(s) that the client may use.")
  private Set<String> scopes;

  @Schema(description = "The client configuration settings.")
  private Map<String, Object> clientSettings;

  @Schema(description = "The token configuration settings.")
  private Map<String, Object> tokenSettings;

  /**
   * AngusGM Client Info.
   */
  @Length(max = MAX_DESC_LENGTH)
  @Schema(description = "Client description.", maxLength = MAX_NAME_LENGTH_X2)
  private String description;

  @Schema(description = "Client enabled or disabled status.", defaultValue = "true")
  private Boolean enabled;

  @Length(max = MAX_KEY_LENGTH_X2)
  @Schema(description = "The business tag of client.", maxLength = MAX_KEY_LENGTH_X2)
  private String bizTag;

}
