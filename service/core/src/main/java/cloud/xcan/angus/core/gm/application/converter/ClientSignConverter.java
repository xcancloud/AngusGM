package cloud.xcan.angus.core.gm.application.converter;

import static cloud.xcan.angus.spec.experimental.BizConstant.AuthKey.MAX_TOKEN_VALIDITY_PERIOD;
import static cloud.xcan.angus.spec.experimental.BizConstant.AuthKey.SIGN2P_TOKEN_CLIENT_SCOPE;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static java.lang.String.format;

import cloud.xcan.angus.api.commonlink.AuthConstant;
import cloud.xcan.angus.api.commonlink.client.Client2pSignupBiz;
import cloud.xcan.angus.api.commonlink.client.ClientSource;
import cloud.xcan.angus.api.enums.Platform;
import cloud.xcan.angus.security.client.CustomOAuth2RegisteredClient;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientCredentialsAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2ClientCredentialsAuthenticationConverter;

public class ClientSignConverter {

  public static CustomOAuth2RegisteredClient privateSignupToDomain(String clientId,
      Client2pSignupBiz signupBiz, Long tenantId, String tenantName, Long resourceId) {
    return CustomOAuth2RegisteredClient
        .with(UUID.randomUUID().toString())
        .clientId(clientId)
        .clientIdIssuedAt(Instant.now())
        .clientSecret(UUID.randomUUID().toString())
        .clientSecretExpiresAt(null)
        .clientName(format(AuthConstant.SIGN2P_CLIENT_NAME_FMT, tenantId,
            signupBiz.name().toLowerCase(), resourceId))
        .clientAuthenticationMethods(methods -> {
          methods.add(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
          methods.add(ClientAuthenticationMethod.CLIENT_SECRET_POST);
        })
        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
        .scope(SIGN2P_TOKEN_CLIENT_SCOPE)
        .clientSettings(ClientSettings.builder()
            .requireProofKey(false)
            .requireAuthorizationConsent(false)
            .build())
        .tokenSettings(TokenSettings.builder()
            .accessTokenFormat(OAuth2TokenFormat.REFERENCE)
            .accessTokenTimeToLive(MAX_TOKEN_VALIDITY_PERIOD)
            .build())
        .description(format("Tenant private `%s` oauth2 client", signupBiz.name()))
        .enabled(true)
        .platform(Platform.XCAN_2P.getValue())
        .source(ClientSource.XCAN_2P_SIGNIN.getValue())
        .bizTag(null)
        .tenantId(tenantId.toString())
        .tenantName(tenantName)
        .createdBy(getUserId().toString())
        .createdDate(Instant.now())
        .lastModifiedBy(getUserId().toString())
        .lastModifiedDate(Instant.now())
        .build();
  }

  /**
   * @see OAuth2ClientCredentialsAuthenticationConverter#convert(HttpServletRequest)
   */
  public static Authentication convertClientSignInAuthentication(
      Set<String> requestedScopes, OAuth2ClientAuthenticationToken clientAuthenticationToken) {
    return new OAuth2ClientCredentialsAuthenticationToken(clientAuthenticationToken,
        requestedScopes, new HashMap<>());
  }

}
