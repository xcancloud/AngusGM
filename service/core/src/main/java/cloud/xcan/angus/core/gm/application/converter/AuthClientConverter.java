package cloud.xcan.angus.core.gm.application.converter;

import static cloud.xcan.angus.api.commonlink.AuthConstant.SYS_TOKEN_CLIENT_DESC_FMT;
import static cloud.xcan.angus.api.commonlink.AuthConstant.SYS_TOKEN_CLIENT_ID_FMT;
import static cloud.xcan.angus.api.enums.Platform.XCAN_TP;
import static cloud.xcan.angus.spec.experimental.BizConstant.AuthKey.SYS_TOKEN_CLIENT_SCOPE;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getTenantId;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getTenantName;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static cloud.xcan.angus.spec.utils.DateUtils.asInstant;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;
import static java.lang.String.format;

import cloud.xcan.angus.api.commonlink.AuthConstant;
import cloud.xcan.angus.api.commonlink.client.ClientSource;
import cloud.xcan.angus.security.client.CustomOAuth2RegisteredClient;
import cloud.xcan.angus.spec.principal.Principal;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;


public class AuthClientConverter {

  public static CustomOAuth2RegisteredClient toSystemTokenToDomain(String tokenName,
      @Nullable LocalDateTime expireDate) {
    return CustomOAuth2RegisteredClient
        .with(UUID.randomUUID().toString())
        .clientId(getSystemTokenClientId(tokenName, ClientSource.XCAN_SYS_TOKEN))
        .clientIdIssuedAt(Instant.now())
        .clientSecret(UUID.randomUUID().toString())
        .clientSecretExpiresAt(asInstant(expireDate))
        .clientName(tokenName)
        .clientAuthenticationMethods(methods -> {
          methods.add(ClientAuthenticationMethod.CLIENT_SECRET_POST);
          methods.add(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
        })
        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
        .scope(SYS_TOKEN_CLIENT_SCOPE)
        .clientSettings(ClientSettings.builder().requireAuthorizationConsent(false).build())
        .tokenSettings(TokenSettings.builder().accessTokenFormat(OAuth2TokenFormat.REFERENCE)
            .accessTokenTimeToLive(getExpireSecond(expireDate)).build())
        .description(getSystemTokenClientDesc(tokenName, ClientSource.XCAN_SYS_TOKEN))
        .enabled(true)
        .platform(XCAN_TP.getValue())
        .source(ClientSource.XCAN_SYS_TOKEN.getValue())
        .bizTag(ClientSource.XCAN_SYS_TOKEN.getValue())
        .tenantId(getTenantId().toString())
        .tenantName(getTenantName())
        .createdBy(getUserId().toString())
        .createdDate(Instant.now())
        .lastModifiedBy(getUserId().toString())
        .lastModifiedDate(Instant.now())
        .build();
  }

  private static Duration getExpireSecond(LocalDateTime expireDate) {
    if (isNull(expireDate)) {
      return Duration.ofSeconds(AuthConstant.DEFAULT_TOKEN_EXPIRE_SECOND);
    }
    return Duration.between(LocalDateTime.now(), expireDate);
  }

  public static String getSystemTokenClientId(String tokenName, ClientSource source) {
    Principal principal = PrincipalContext.get();
    if (Objects.requireNonNull(source) == ClientSource.XCAN_SYS_TOKEN) {
      if (tokenName == null) {
        throw new IllegalArgumentException("Token name cannot be null");
      }
      return format(SYS_TOKEN_CLIENT_ID_FMT, principal.getTenantId(), tokenName.trim().hashCode());
    }
    throw new IllegalStateException(format("Generate the clientId based on %s is not supported",
        source.getValue()));
  }

  public static String getSystemTokenClientDesc(String tokenName, ClientSource source) {
    Principal principal = PrincipalContext.get();
    if (Objects.requireNonNull(source) == ClientSource.XCAN_SYS_TOKEN) {
      return format(SYS_TOKEN_CLIENT_DESC_FMT, principal.getTenantId(), tokenName);
    }
    throw new IllegalStateException(
        format("Generate the client description based on %s is not supported", source.getValue()));
  }

}
