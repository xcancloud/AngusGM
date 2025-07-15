package cloud.xcan.angus.core.gm.interfaces.auth.facade.internal.assembler;

import static cloud.xcan.angus.api.commonlink.client.ClientSource.XCAN_SYS_TOKEN;
import static cloud.xcan.angus.api.enums.Platform.XCAN_TP;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static cloud.xcan.angus.spec.utils.DateUtils.asInstant;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;
import static cloud.xcan.angus.spec.utils.ObjectUtils.stringSafe;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.client.ClientSource;
import cloud.xcan.angus.api.enums.Platform;
import cloud.xcan.angus.api.gm.client.dto.AuthClientAddDto;
import cloud.xcan.angus.api.gm.client.dto.AuthClientReplaceDto;
import cloud.xcan.angus.api.gm.client.dto.AuthClientUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.auth.facade.vo.AuthClientDetailVo;
import cloud.xcan.angus.security.client.CustomOAuth2RegisteredClient;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

public class AuthClientAssembler {

  public static CustomOAuth2RegisteredClient addDtoToDomain(AuthClientAddDto dto) {
    return CustomOAuth2RegisteredClient
        .with(stringSafe(dto.getId(), UUID.randomUUID().toString()))
        .clientId(dto.getClientId())
        .clientIdIssuedAt(Instant.now())
        .clientSecret(dto.getClientSecret())
        .clientSecretExpiresAt(asInstant(dto.getClientSecretExpiresAt()))
        .clientName(dto.getClientName())
        .clientAuthenticationMethods(methods -> {
          if (isNotEmpty(dto.getClientAuthenticationMethods())) {
            for (String method : dto.getClientAuthenticationMethods()) {
              methods.add(new ClientAuthenticationMethod(method));
            }
          }
        })
        .authorizationGrantTypes(types -> {
          if (isNotEmpty(dto.getAuthorizationGrantTypes())) {
            for (String type : dto.getAuthorizationGrantTypes()) {
              types.add(new AuthorizationGrantType(type));
            }
          }
        })
        .redirectUris(uris -> {
          if (isNotEmpty(dto.getRedirectUris())) {
            uris.addAll(dto.getRedirectUris());
          }
        })
        .postLogoutRedirectUris(uris -> {
          if (isNotEmpty(dto.getPostLogoutRedirectUris())) {
            uris.addAll(dto.getPostLogoutRedirectUris());
          }
        })
        .scopes(scopes -> {
          if (isNotEmpty(dto.getScopes())) {
            scopes.addAll(dto.getScopes());
          }
        })
        .clientSettings(ClientSettings.withSettings(dto.getClientSettings()).build())
        .tokenSettings(TokenSettings.withSettings(dto.getTokenSettings()).build())
        .description(dto.getDescription())
        .enabled(nullSafe(dto.getEnabled(), true))
        .platform(nullSafe(dto.getPlatform(), XCAN_TP).getValue())
        .source(nullSafe(dto.getSource(), XCAN_SYS_TOKEN).getValue())
        .bizTag(dto.getBizTag())
        .tenantId(dto.getTenantId())
        .tenantName(dto.getTenantName())
        .createdBy(getUserId().toString())
        .createdDate(Instant.now())
        .lastModifiedBy(getUserId().toString())
        .lastModifiedDate(Instant.now())
        .build();
  }

  public static CustomOAuth2RegisteredClient replaceDtoToDomain(AuthClientReplaceDto dto) {
    return CustomOAuth2RegisteredClient
        .with(dto.getId())
        .clientId(isNull(dto.getId()) ? dto.getClientId() : null)
        .clientIdIssuedAt(isNull(dto.getId()) ? Instant.now() : null)
        .clientSecret(dto.getClientSecret())
        .clientSecretExpiresAt(asInstant(dto.getClientSecretExpiresAt()))
        .clientName(dto.getClientName())
        .clientAuthenticationMethods(methods -> {
          if (isNotEmpty(dto.getClientAuthenticationMethods())) {
            for (String method : dto.getClientAuthenticationMethods()) {
              methods.add(new ClientAuthenticationMethod(method));
            }
          }
        })
        .authorizationGrantTypes(types -> {
          if (isNotEmpty(dto.getAuthorizationGrantTypes())) {
            for (String type : dto.getAuthorizationGrantTypes()) {
              types.add(new AuthorizationGrantType(type));
            }
          }
        })
        .redirectUris(uris -> {
          if (isNotEmpty(dto.getRedirectUris())) {
            uris.addAll(dto.getRedirectUris());
          }
        })
        .postLogoutRedirectUris(uris -> {
          if (isNotEmpty(dto.getPostLogoutRedirectUris())) {
            uris.addAll(dto.getPostLogoutRedirectUris());
          }
        })
        .scopes(scopes -> {
          if (isNotEmpty(dto.getScopes())) {
            scopes.addAll(dto.getScopes());
          }
        })
        .clientSettings(ClientSettings.withSettings(dto.getClientSettings()).build())
        .tokenSettings(TokenSettings.withSettings(dto.getTokenSettings()).build())
        .description(dto.getDescription())
        .enabled(nullSafe(dto.getEnabled(), true))
        .platform(isNull(dto.getId()) ? nullSafe(dto.getPlatform(), XCAN_TP).getValue() : null)
        .source(isNull(dto.getId()) ? nullSafe(dto.getSource(), XCAN_SYS_TOKEN).getValue() : null)
        .bizTag(dto.getBizTag())
        .tenantId(isNull(dto.getId()) ? dto.getTenantId() : null)
        .tenantName(isNull(dto.getId()) ? dto.getTenantName() : null)
        .createdBy(isNull(dto.getId()) ? getUserId().toString() : null)
        .createdDate(isNull(dto.getId()) ? Instant.now() : null)
        .lastModifiedBy(getUserId().toString())
        .lastModifiedDate(Instant.now())
        .build();
  }

  public static CustomOAuth2RegisteredClient updateDtoToDomain(AuthClientUpdateDto dto) {
    return CustomOAuth2RegisteredClient
        .with(dto.getId())
        .clientSecret(dto.getClientSecret())
        .clientSecretExpiresAt(asInstant(dto.getClientSecretExpiresAt()))
        .clientName(dto.getClientName())
        .clientAuthenticationMethods(methods -> {
          if (isNotEmpty(dto.getClientAuthenticationMethods())) {
            for (String method : dto.getClientAuthenticationMethods()) {
              methods.add(new ClientAuthenticationMethod(method));
            }
          }
        })
        .authorizationGrantTypes(types -> {
          if (isNotEmpty(dto.getAuthorizationGrantTypes())) {
            for (String type : dto.getAuthorizationGrantTypes()) {
              types.add(new AuthorizationGrantType(type));
            }
          }
        })
        .redirectUris(uris -> {
          if (isNotEmpty(dto.getRedirectUris())) {
            uris.addAll(dto.getRedirectUris());
          }
        })
        .postLogoutRedirectUris(uris -> {
          if (isNotEmpty(dto.getPostLogoutRedirectUris())) {
            uris.addAll(dto.getPostLogoutRedirectUris());
          }
        })
        .scopes(scopes -> {
          if (isNotEmpty(dto.getScopes())) {
            scopes.addAll(dto.getScopes());
          }
        })
        .clientSettings(nonNull(dto.getClientSettings())
            ? ClientSettings.withSettings(dto.getClientSettings()).build() : null)
        .tokenSettings(nonNull(dto.getTokenSettings())
            ? TokenSettings.withSettings(dto.getTokenSettings()).build() : null)
        .description(dto.getDescription())
        .enabled(dto.getEnabled())
        .bizTag(dto.getBizTag())
        .lastModifiedBy(getUserId().toString())
        .lastModifiedDate(Instant.now())
        .build();
  }

  public static AuthClientDetailVo toDetailVo(CustomOAuth2RegisteredClient client) {
    return new AuthClientDetailVo().setId(client.getId())
        .setClientId(client.getClientId())
        .setClientIdIssuedAt(nonNull(client.getClientIdIssuedAt())
            ? LocalDateTime.from(client.getClientIdIssuedAt()) : null)
        .setClientSecret(client.getClientSecret())
        .setClientIdIssuedAt(nonNull(client.getClientSecretExpiresAt())
            ? LocalDateTime.from(client.getClientSecretExpiresAt()) : null)
        .setClientName(client.getClientName())
        .setClientAuthenticationMethods(client.getClientAuthenticationMethods().stream()
            .map(ClientAuthenticationMethod::getValue).collect(Collectors.toSet()))
        .setAuthorizationGrantTypes(client.getAuthorizationGrantTypes().stream()
            .map(AuthorizationGrantType::getValue).collect(Collectors.toSet()))
        .setRedirectUris(client.getRedirectUris())
        .setPostLogoutRedirectUris(client.getPostLogoutRedirectUris())
        .setScopes(client.getScopes())
        .setClientSettings(client.getClientSettings().getSettings())
        .setTokenSettings(client.getTokenSettings().getSettings())
        .setDescription(client.getDescription())
        .setEnabled(client.isEnabled())
        .setPlatform(isNotEmpty(client.getPlatform())
            ? Platform.valueOf(client.getPlatform()) : null)
        .setSource(isNotEmpty(client.getSource())
            ? ClientSource.valueOf(client.getSource()) : null)
        .setBizTag(client.getBizTag())
        .setTenantId(client.getTenantId())
        .setTenantName(client.getTenantName())
        .setCreatedBy(client.getCreatedBy())
        .setCreatedDate(LocalDateTime.from(client.getCreatedDate()))
        .setLastModifiedBy(client.getLastModifiedBy())
        .setLastModifiedDate(LocalDateTime.from(client.getLastModifiedDate()));
  }

}
