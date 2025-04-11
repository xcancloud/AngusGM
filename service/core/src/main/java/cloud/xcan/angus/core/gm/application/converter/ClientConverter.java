package cloud.xcan.angus.core.gm.application.converter;

import static cloud.xcan.angus.api.commonlink.AASConstant.SYS_TOKEN_CLIENT_DESC_FMT;
import static cloud.xcan.angus.api.commonlink.AASConstant.SYS_TOKEN_CLIENT_ID_FMT;
import static cloud.xcan.angus.api.commonlink.client.ClientSource.XCAN_USER_TOKEN;
import static cloud.xcan.angus.api.enums.Platform.XCAN_TP;
import static cloud.xcan.angus.spec.experimental.BizConstant.AuthKey.SYS_TOKEN_CLIENT_SCOPE;
import static cloud.xcan.angus.spec.experimental.BizConstant.ClientSource.XCAN_SYS_TOKEN;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getTenantId;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getTenantName;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.AASConstant;
import cloud.xcan.angus.api.commonlink.client.ClientSource;
import cloud.xcan.angus.security.client.CustomOAuth2RegisteredClient;
import cloud.xcan.angus.spec.principal.Principal;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;


public class ClientConverter {

  public static CustomOAuth2RegisteredClient toSystemTokenToDomain(String tokenName,
      @Nullable LocalDateTime expireDate) {
    return CustomOAuth2RegisteredClient
        .with(UUID.randomUUID().toString())
        .clientId(getSystemTokenClientId(tokenName, ClientSource.XCAN_SYS_TOKEN))
        .clientIdIssuedAt(Instant.now())
        .clientSecret(UUID.randomUUID().toString())
        .clientSecretExpiresAt(nonNull(expireDate) ? Instant.from(expireDate) : null)
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
        .source(XCAN_USER_TOKEN.getTokenPrefix())
        .bizTag(XCAN_SYS_TOKEN)
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
      return Duration.ofSeconds(AASConstant.DEFAULT_TOKEN_EXPIRE_SECOND);
    }
    return Duration.between(LocalDateTime.now(), expireDate);
  }

  public static String getSystemTokenClientId(String tokenName, ClientSource source) {
    Principal principal = PrincipalContext.get();
    return switch (source) {
      case XCAN_SYS_TOKEN ->
          String.format(SYS_TOKEN_CLIENT_ID_FMT, principal.getTenantId(), tokenName.trim());
      default -> throw new IllegalStateException(
          String.format("Generate the clientId based on %s is not supported", source.getValue()));
    };
  }

  public static String getSystemTokenClientDesc(String tokenName, ClientSource source) {
    Principal principal = PrincipalContext.get();
    return switch (source) {
      case XCAN_SYS_TOKEN ->
          String.format(SYS_TOKEN_CLIENT_DESC_FMT, principal.getTenantId(), tokenName);
      default -> throw new IllegalStateException(
          String.format("Generate the client description based on %s is not supported",
              source.getValue()));
    };
  }

}
