package cloud.xcan.angus.core.gm.application.cmd.authuser.impl;

import static cloud.xcan.angus.api.commonlink.AASConstant.USER_TOKEN_CLIENT_SCOPE;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.gm.application.converter.UserSignConverter.convertClientSuccessAuthentication;
import static cloud.xcan.angus.core.gm.application.converter.UserSignConverter.convertUserSignInAuthentication;
import static cloud.xcan.angus.core.gm.domain.AASCoreMessage.SIGN_IN_PASSWORD_ERROR;
import static cloud.xcan.angus.spec.experimental.BizConstant.AuthKey.ACCESS_TOKEN_EXPIRED_DATE;
import static cloud.xcan.angus.spec.experimental.BizConstant.AuthKey.CUSTOM_ACCESS_TOKEN;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getClientId;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.authuser.AuthUser;
import cloud.xcan.angus.api.enums.SignInType;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.authuser.AuthUserTokenCmd;
import cloud.xcan.angus.core.gm.application.query.authuser.AuthUserQuery;
import cloud.xcan.angus.core.gm.application.query.authuser.AuthUserTokenQuery;
import cloud.xcan.angus.core.gm.application.query.client.ClientQuery;
import cloud.xcan.angus.core.gm.domain.authuser.AuthUserToken;
import cloud.xcan.angus.core.gm.domain.authuser.AuthUserTokenRepo;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.security.authentication.OAuth2AccessTokenGenerator;
import cloud.xcan.angus.security.authentication.dao.DaoAuthenticationProvider;
import cloud.xcan.angus.security.authentication.password.OAuth2PasswordAuthenticationProvider;
import cloud.xcan.angus.security.client.CustomOAuth2RegisteredClient;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import jakarta.annotation.Resource;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.transaction.annotation.Transactional;

@Biz
public class AuthUserTokenCmdImpl extends CommCmd<AuthUserToken, Long> implements AuthUserTokenCmd {

  @Resource
  private AuthUserTokenRepo authUserTokenRepo;

  @Resource
  private AuthUserTokenQuery authUserTokenQuery;

  @Resource
  private AuthUserQuery authUserQuery;

  @Resource
  private ClientQuery clientQuery;

  @Resource
  private PasswordEncoder passwordEncoder;

  @Resource
  private AuthenticationManager authenticationManager;

  @Resource
  private OAuth2AuthorizationService oauth2AuthorizationService;

  @Resource
  private DaoAuthenticationProvider daoAuthenticationProvider;

  /**
   * @see OAuth2PasswordAuthenticationProvider#authenticate(Authentication)
   * @see OAuth2AccessTokenGenerator#generate(OAuth2TokenContext)
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public AuthUserToken add(AuthUserToken userToken) {
    return new BizTemplate<AuthUserToken>() {
      final Long currentUserId = getUserId();
      CustomOAuth2RegisteredClient clientDb;
      AuthUser userDb;

      @Override
      protected void checkParams() {
        // Check the current user password
        userDb = authUserQuery.checkAndFind(currentUserId);
        // Fix:: There is no PasswordEncoder mapped for the id \"null\" when password not set
        assertTrue(isNotEmpty(userToken.getPassword())
                && passwordEncoder.matches(userToken.getPassword(), userDb.getPassword()),
            SIGN_IN_PASSWORD_ERROR);
        // Check the client existed
        clientDb = clientQuery.checkAndFind(getClientId());
        // Check the token name existed
        authUserTokenQuery.checkNameNotExisted(userToken);
        // Check the user token quota
        authUserTokenQuery.checkTokenQuota(currentUserId, 1);
      }

      @Override
      protected AuthUserToken process() {
        // Cached to context for load UserDetail
        daoAuthenticationProvider.getUserCache().putUserInCache(userDb.getId(),
            userDb.getUsername(), userDb);

        // Set expired date for cloud.xcan.angus.security.authentication.OAuth2AccessTokenGenerator#generate(OAuth2TokenContext context)
        PrincipalContext.addExtension(CUSTOM_ACCESS_TOKEN, true);
        // The token is permanently valid when the value is null.
        if (nonNull(userToken.getExpiredDate())) {
          PrincipalContext.addExtension(ACCESS_TOKEN_EXPIRED_DATE,
              Instant.from(userToken.getExpiredDate()));
        }

        // Submit OAuth2 login authentication
        OAuth2ClientAuthenticationToken clientAuthenticationToken
            = convertClientSuccessAuthentication(clientDb);
        Authentication userAuthenticationToken = convertUserSignInAuthentication(
            SignInType.ACCOUNT_PASSWORD, currentUserId, userDb.getUsername(), userDb.getPassword(),
            Set.of(USER_TOKEN_CLIENT_SCOPE), clientAuthenticationToken);
        OAuth2AccessTokenAuthenticationToken result = (OAuth2AccessTokenAuthenticationToken)
            authenticationManager.authenticate(userAuthenticationToken);
        OAuth2AccessToken accessToken = result.getAccessToken();

        // Save user token
        userToken.setDecryptedValue(accessToken.getTokenValue());
        userToken.setValue(authUserTokenQuery.encryptValue(accessToken.getTokenValue()));
        userToken.setId(uidGenerator.getUID());
        insert0(userToken);
        return userToken;
      }
    }.execute();
  }

  /**
   * Note: The access_token will automatically expire in auth2. After expiration, the configuration
   * needs to be manually deleted by the user.
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(Set<Long> ids) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        List<AuthUserToken> userTokensDb = authUserTokenQuery.find0(ids);
        if (isEmpty(userTokensDb)) {
          return null;
        }

        for (AuthUserToken userToken : userTokensDb) {
          String accessToken = authUserTokenQuery.decryptValue(userToken.getValue());
          OAuth2Authorization authorizationDb = oauth2AuthorizationService.findByToken(
              accessToken, null);
          if (nonNull(authorizationDb)) {
            oauth2AuthorizationService.remove(authorizationDb);
          }
        }

        authUserTokenRepo.deleteByIdIn(ids);
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<AuthUserToken, Long> getRepository() {
    return authUserTokenRepo;
  }
}
