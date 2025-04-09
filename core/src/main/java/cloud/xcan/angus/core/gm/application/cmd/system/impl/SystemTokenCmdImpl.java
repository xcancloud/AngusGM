package cloud.xcan.angus.core.gm.application.cmd.system.impl;

import static cloud.xcan.angus.api.commonlink.client.ClientSource.XCAN_SYS_TOKEN;
import static cloud.xcan.angus.core.gm.application.cmd.client.impl.ClientSignCmdImpl.submitOauth2ClientSignInRequest;
import static cloud.xcan.angus.core.gm.application.converter.ClientConverter.toSystemTokenToDomain;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.checkTenantSysAdmin;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.service.ServiceResource;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.client.ClientCmd;
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
import cloud.xcan.angus.security.authentication.OAuth2AccessTokenGenerator;
import cloud.xcan.angus.security.client.CustomOAuth2ClientRepository;
import cloud.xcan.angus.security.client.CustomOAuth2RegisteredClient;
import cloud.xcan.angus.spec.experimental.BizConstant.AuthKey;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientCredentialsAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.transaction.annotation.Transactional;


@Biz
public class SystemTokenCmdImpl extends CommCmd<SystemToken, Long> implements SystemTokenCmd {

  @Resource
  private SystemTokenRepo systemTokenRepo;

  @Resource
  private SystemTokenResourceRepo systemTokenResourceRepo;

  @Resource
  private SystemTokenQuery systemTokenQuery;

  @Resource
  private ClientCmd clientCmd;

  @Resource
  private ApiQuery apiQuery;

  @Resource
  private AuthenticationManager authenticationManager;

  @Resource
  private OAuth2AuthorizationService oauth2AuthorizationService;

  @Resource
  private CustomOAuth2ClientRepository customOAuth2ClientRepository;

  /**
   * @see OAuth2ClientCredentialsAuthenticationProvider#authenticate(Authentication)
   * @see OAuth2AccessTokenGenerator#generate(OAuth2TokenContext)
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public SystemToken add(SystemToken systemToken, List<SystemTokenResource> resources) {
    return new BizTemplate<SystemToken>() {
      final Long optTenantId = getOptTenantId();
      Map<String, List<ServiceResource>> serviceResourceMap;

      @Override
      protected void checkParams() {
        // Check the current user must be a system administrator
        checkTenantSysAdmin();
        // Check the name not existed
        systemTokenQuery.checkNameNotExisted(systemToken);
        // Check the resources existed
        serviceResourceMap = apiQuery.checkAndFindResource(resources.stream()
            .map(SystemTokenResource::getResource).collect(Collectors.toSet()));
        if (systemToken.isApiAuth()) {
          // Check the apis existed
          List<Long> apiIds = resources.stream().map(x -> Long.valueOf(x.getAuthority()))
              .collect(Collectors.toList());
          apiQuery.checkAndFind(apiIds, true);
        }
        // Check the token quota
        systemTokenQuery.checkTokenQuota(optTenantId, 1);
      }

      @Override
      protected SystemToken process() {
        // Generate client for system token
        CustomOAuth2RegisteredClient client = toSystemTokenToDomain(systemToken.getName(),
            systemToken.getExpiredDate());
        customOAuth2ClientRepository.save(client);

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

        // Save system token
        String systemAccessToken = result.get(AuthKey.ACCESS_TOKEN);
        systemToken.setDecryptedValue(systemAccessToken);
        systemToken.setValue(systemTokenQuery.encryptValue(systemAccessToken));
        systemToken.setId(uidGenerator.getUID());
        insert0(systemToken);

        // Save system token resources
        // Important:: Resources must be unique under all services
        List<SystemTokenResource> tokenResources = toSystemTokenResource(systemToken.getId(),
            resources, serviceResourceMap);
        systemTokenResourceRepo.saveAll(tokenResources);
        return systemToken;
      }
    }.execute();
  }

  /**
   * Note: The access_token will automatically expire in auth2. After expiration, the configuration
   * needs to be manually deleted by the user.
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public Void delete(HashSet<Long> ids) {
    return new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        // Check the current user must be a system administrator
        checkTenantSysAdmin();
      }

      @Override
      protected Void process() {
        List<SystemToken> systemTokensDb = systemTokenRepo.findAllById(ids);
        if (isEmpty(systemTokensDb)) {
          return null;
        }

        for (SystemToken systemToken : systemTokensDb) {
          clientCmd.deleteSystemTokenClient(systemToken.getName(), XCAN_SYS_TOKEN);
          String accessToken = systemTokenQuery.decryptValue(systemToken.getValue());
          OAuth2Authorization authorizationDb = oauth2AuthorizationService.findByToken(
              accessToken, null);
          if (nonNull(authorizationDb)) {
            oauth2AuthorizationService.remove(authorizationDb);
          }
        }
        systemTokenRepo.deleteByIdIn(ids);
        systemTokenResourceRepo.deleteBySystemTokenIdIn(ids);
        return null;
      }
    }.execute();
  }

  @Override
  public void deleteByApiIdIn(Collection<Long> ids) {
    systemTokenResourceRepo.deleteByAuthorityIn(ids.stream().map(String::valueOf)
        .collect(Collectors.toSet()));
  }

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
