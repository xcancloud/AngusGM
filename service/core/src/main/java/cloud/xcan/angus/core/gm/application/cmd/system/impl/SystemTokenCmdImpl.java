package cloud.xcan.angus.core.gm.application.cmd.system.impl;

import static cloud.xcan.angus.api.commonlink.client.ClientSource.XCAN_SYS_TOKEN;
import static cloud.xcan.angus.core.gm.application.cmd.auth.impl.AuthClientSignCmdImpl.submitOauth2ClientSignInRequest;
import static cloud.xcan.angus.core.gm.application.converter.AuthClientConverter.toSystemTokenToDomain;
import static cloud.xcan.angus.core.gm.domain.operation.OperationResourceType.SYSTEM_TOKEN;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.CREATED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.DELETED;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.checkTenantSysAdmin;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.service.ServiceResource;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.auth.AuthClientCmd;
import cloud.xcan.angus.core.gm.application.cmd.operation.OperationLogCmd;
import cloud.xcan.angus.core.gm.application.cmd.system.SystemTokenCmd;
import cloud.xcan.angus.core.gm.application.query.api.ApiQuery;
import cloud.xcan.angus.core.gm.application.query.system.SystemTokenQuery;
import cloud.xcan.angus.core.gm.domain.system.SystemToken;
import cloud.xcan.angus.core.gm.domain.system.SystemTokenRepo;
import cloud.xcan.angus.core.gm.domain.system.resource.SystemTokenResource;
import cloud.xcan.angus.core.gm.domain.system.resource.SystemTokenResourceRepo;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.remote.message.AbstractResultMessageException;
import cloud.xcan.angus.remote.message.SysException;
import cloud.xcan.angus.security.client.CustomOAuth2ClientRepository;
import cloud.xcan.angus.security.client.CustomOAuth2RegisteredClient;
import cloud.xcan.angus.spec.experimental.BizConstant.AuthKey;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * Implementation of system token command operations.
 * </p>
 * <p>
 * Manages system token lifecycle including creation, deletion, and OAuth2 integration.
 * Provides secure token generation with proper authentication and authorization.
 * </p>
 * <p>
 * Integrates with OAuth2 authorization server for token management and client registration.
 * Supports API authentication and resource-based access control.
 * </p>
 */
@Biz
public class SystemTokenCmdImpl extends CommCmd<SystemToken, Long> implements SystemTokenCmd {

  @Resource
  private SystemTokenRepo systemTokenRepo;
  @Resource
  private SystemTokenResourceRepo systemTokenResourceRepo;
  @Resource
  private SystemTokenQuery systemTokenQuery;
  @Resource
  private AuthClientCmd clientCmd;
  @Resource
  private ApiQuery apiQuery;
  @Resource
  private OAuth2AuthorizationService oauth2AuthorizationService;
  @Resource
  private CustomOAuth2ClientRepository customOAuth2ClientRepository;
  @Resource
  private SystemTokenCmd systemTokenCmd;
  @Resource
  private OperationLogCmd operationLogCmd;
  @Resource
  private PasswordEncoder passwordEncoder;

  /**
   * <p>
   * Creates a new system token with OAuth2 integration.
   * </p>
   * <p>
   * Validates administrator permissions, token name uniqueness, and resource existence.
   * Creates OAuth2 client, performs authentication, and saves encrypted token.
   * </p>
   * <p>
   * Integrates with OAuth2ClientCredentialsAuthenticationProvider and OAuth2AccessTokenGenerator
   * for secure token generation and management.
   * </p>
   */
  @Override
  public SystemToken add(SystemToken systemToken, List<SystemTokenResource> resources) {
    return new BizTemplate<SystemToken>() {
      final Long optTenantId = getOptTenantId();
      Map<String, List<ServiceResource>> serviceResourceMap;

      @Override
      protected void checkParams() {
        // Verify current user must be a system administrator
        checkTenantSysAdmin();
        // Verify token name uniqueness
        systemTokenQuery.checkNameNotExisted(systemToken);
        // Verify resources exist
        serviceResourceMap = apiQuery.checkAndFindResource(resources.stream()
            .map(SystemTokenResource::getResource).collect(Collectors.toSet()));
        if (systemToken.isApiAuth()) {
          // Verify APIs exist for API authentication
          List<Long> apiIds = resources.stream().map(x -> Long.valueOf(x.getAuthority()))
              .collect(Collectors.toList());
          apiQuery.checkAndFind(apiIds, true);
        }
        // Verify token quota
        systemTokenQuery.checkTokenQuota(optTenantId, 1);
      }

      @Override
      protected SystemToken process() {
        // Create OAuth2 client for system token
        CustomOAuth2RegisteredClient client = systemTokenCmd.saveCustomOAuth2RegisteredClient(
            systemToken);

        // Submit OAuth2 login authentication
        Map<String, String> result;
        try {
          result = submitOauth2ClientSignInRequest(client.getClientId(), client.getClientSecret(),
              String.join(" ", client.getScopes()));
        } catch (Throwable e) {
          if (e instanceof AbstractResultMessageException) {
            throw (AbstractResultMessageException) e;
          }
          throw new SysException(e.getMessage());
        }

        // Save system token with encrypted access token
        systemTokenCmd.saveSystemAccessToken(systemToken, resources, serviceResourceMap, result);

        // Log operation for audit
        operationLogCmd.add(SYSTEM_TOKEN, systemToken, CREATED);
        return systemToken;
      }
    }.execute();
  }

  /**
   * <p>
   * Saves OAuth2 registered client for system token.
   * </p>
   * <p>
   * Creates and configures OAuth2 client with proper credentials and expiration.
   * Removes existing client with same ID before creating new one.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public CustomOAuth2RegisteredClient saveCustomOAuth2RegisteredClient(SystemToken systemToken) {
    // Generate OAuth2 client for system token
    CustomOAuth2RegisteredClient client = toSystemTokenToDomain(systemToken.getName(),
        systemToken.getExpiredDate());
    customOAuth2ClientRepository.deleteByClientId(client.getClientId());
    client.setClientSecret(passwordEncoder.encode(client.getClientSecret()));
    customOAuth2ClientRepository.save(client);
    return client;
  }

  /**
   * <p>
   * Saves system access token with encryption and resource associations.
   * </p>
   * <p>
   * Encrypts access token, saves system token, and creates resource associations.
   * Ensures resources are unique across all services.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void saveSystemAccessToken(SystemToken systemToken, List<SystemTokenResource> resources,
      Map<String, List<ServiceResource>> serviceResourceMap, Map<String, String> result) {
    String systemAccessToken = result.get(AuthKey.ACCESS_TOKEN);
    systemToken.setDecryptedValue(systemAccessToken);
    systemToken.setValue(systemTokenQuery.encryptValue(systemAccessToken));
    systemToken.setId(uidGenerator.getUID());
    insert0(systemToken);

    // Save system token resources
    // Important: Resources must be unique under all services
    List<SystemTokenResource> tokenResources = toSystemTokenResource(systemToken.getId(),
        resources, serviceResourceMap);
    systemTokenResourceRepo.saveAll(tokenResources);
  }

  /**
   * <p>
   * Deletes system tokens and associated OAuth2 authorizations.
   * </p>
   * <p>
   * Removes OAuth2 authorizations and client registrations for each token.
   * Note: Access tokens automatically expire in OAuth2. After expiration,
   * configuration needs to be manually deleted by the user.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public Void delete(HashSet<Long> ids) {
    return new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        // Verify current user must be a system administrator
        checkTenantSysAdmin();
      }

      @Override
      protected Void process() {
        List<SystemToken> systemTokensDb = systemTokenRepo.findAllById(ids);
        if (isEmpty(systemTokensDb)) {
          return null;
        }

        for (SystemToken systemToken : systemTokensDb) {
          String accessToken = systemTokenQuery.decryptValue(systemToken.getValue());
          try {
            // Remove OAuth2 authorization
            OAuth2Authorization authorizationDb = oauth2AuthorizationService.findByToken(
                accessToken, null);
            if (nonNull(authorizationDb)) {
              oauth2AuthorizationService.remove(authorizationDb);
            }
          } catch (Exception e) {
            // NOOP - handle authorization removal errors gracefully
          }
          // Delete OAuth2 client registration
          clientCmd.deleteSystemTokenClient(systemToken.getName(), XCAN_SYS_TOKEN);
        }
        // Delete system tokens and resources
        systemTokenRepo.deleteByIdIn(ids);
        systemTokenResourceRepo.deleteBySystemTokenIdIn(ids);

        // Log operation for audit
        operationLogCmd.addAll(SYSTEM_TOKEN, systemTokensDb, DELETED);
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Deletes system token resources by API IDs.
   * </p>
   * <p>
   * Removes all system token resources associated with the specified API IDs.
   * </p>
   */
  @Override
  public void deleteByApiIdIn(Collection<Long> ids) {
    systemTokenResourceRepo.deleteByAuthorityIn(ids.stream().map(String::valueOf)
        .collect(Collectors.toSet()));
  }

  /**
   * <p>
   * Converts system token resources with service code mapping.
   * </p>
   * <p>
   * Maps resources to system token resources with proper service code assignment
   * and unique ID generation.
   * </p>
   */
  private List<SystemTokenResource> toSystemTokenResource(Long systemTokenId,
      List<SystemTokenResource> resources, Map<String, List<ServiceResource>> serviceResourceMap) {
    return resources.stream().map(resource ->
            resource.setId(uidGenerator.getUID())
                .setServiceCode(
                    serviceResourceMap.get(resource.getResource()).get(0).getServiceCode())
                .setSystemTokenId(systemTokenId))
        .collect(Collectors.toList());
  }

  @Override
  protected BaseRepository<SystemToken, Long> getRepository() {
    return this.systemTokenRepo;
  }
}
