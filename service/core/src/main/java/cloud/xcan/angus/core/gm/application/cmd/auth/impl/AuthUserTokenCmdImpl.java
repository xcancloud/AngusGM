package cloud.xcan.angus.core.gm.application.cmd.auth.impl;

import static cloud.xcan.angus.api.commonlink.client.ClientSource.isUserSignIn;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.gm.application.cmd.auth.impl.AuthUserSignCmdImpl.submitOauth2UserSignInRequest;
import static cloud.xcan.angus.core.gm.domain.AuthMessage.SIGN_IN_PASSWORD_ERROR;
import static cloud.xcan.angus.core.gm.domain.operation.OperationResourceType.USER_TOKEN;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.CREATED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.DELETED;
import static cloud.xcan.angus.spec.experimental.BizConstant.AuthKey.ACCESS_TOKEN_EXPIRED_DATE;
import static cloud.xcan.angus.spec.experimental.BizConstant.AuthKey.CUSTOM_ACCESS_TOKEN;
import static cloud.xcan.angus.spec.experimental.BizConstant.AuthKey.CUSTOM_ACCESS_TOKEN_NAME;
import static cloud.xcan.angus.spec.experimental.BizConstant.AuthKey.USER_TOKEN_CLIENT_SCOPE;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getClientId;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static cloud.xcan.angus.spec.principal.PrincipalContext.setRequestAttribute;
import static cloud.xcan.angus.spec.utils.DateUtils.asInstant;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.authuser.AuthUser;
import cloud.xcan.angus.api.enums.SignInType;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.auth.AuthUserTokenCmd;
import cloud.xcan.angus.core.gm.application.cmd.operation.OperationLogCmd;
import cloud.xcan.angus.core.gm.application.query.auth.AuthClientQuery;
import cloud.xcan.angus.core.gm.application.query.auth.AuthUserQuery;
import cloud.xcan.angus.core.gm.application.query.auth.AuthUserTokenQuery;
import cloud.xcan.angus.core.gm.domain.auth.AuthUserToken;
import cloud.xcan.angus.core.gm.domain.auth.AuthUserTokenRepo;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.remote.message.AbstractResultMessageException;
import cloud.xcan.angus.remote.message.SysException;
import cloud.xcan.angus.security.authentication.dao.DaoAuthenticationProvider;
import cloud.xcan.angus.security.client.CustomOAuth2RegisteredClient;
import cloud.xcan.angus.spec.experimental.BizConstant.AuthKey;
import jakarta.annotation.Resource;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of user token command operations for managing user access tokens.
 *
 * <p>This class provides comprehensive functionality for user token management including:</p>
 * <ul>
 *   <li>Creating user access tokens with password verification</li>
 *   <li>Managing token quotas and security settings</li>
 *   <li>Deleting tokens and cleaning up authorizations</li>
 *   <li>Integrating with OAuth2 authorization service</li>
 *   <li>Recording token operation audit logs</li>
 * </ul>
 *
 * <p>The implementation ensures secure token management with proper validation
 * and integration with OAuth2 authorization framework.</p>
 */
@Biz
public class AuthUserTokenCmdImpl extends CommCmd<AuthUserToken, Long> implements AuthUserTokenCmd {

  @Resource
  private AuthUserTokenRepo authUserTokenRepo;
  @Resource
  private AuthUserTokenQuery authUserTokenQuery;
  @Resource
  private AuthUserQuery authUserQuery;
  @Resource
  private AuthClientQuery clientQuery;
  @Resource
  private PasswordEncoder passwordEncoder;
  @Resource
  private OAuth2AuthorizationService oauth2AuthorizationService;
  @Resource
  private DaoAuthenticationProvider daoAuthenticationProvider;
  @Resource
  private OperationLogCmd operationLogCmd;

  /**
   * Creates user access token with comprehensive validation and security.
   *
   * <p>This method performs token creation including:</p>
   * <ul>
   *   <li>Validating current user password</li>
   *   <li>Checking client credentials and permissions</li>
   *   <li>Verifying token name uniqueness</li>
   *   <li>Checking user token quota limits</li>
   *   <li>Generating OAuth2 access token</li>
   *   <li>Storing encrypted token data</li>
   * </ul>
   *
   * <p>This method integrates with OAuth2PasswordAuthenticationProvider and
   * OAuth2AccessTokenGenerator for token generation.</p>
   *
   * @param userToken User token entity to create
   * @return Created user token entity
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
        // Validate current user password
        userDb = authUserQuery.checkAndFind(currentUserId);
        // Note: Handle case when password is not set to avoid encoding issues
        assertTrue(isNotEmpty(userToken.getPassword())
                && passwordEncoder.matches(userToken.getPassword(), userDb.getPassword()),
            SIGN_IN_PASSWORD_ERROR);
        // Validate client exists and supports user sign-in
        clientDb = clientQuery.checkAndFind(getClientId());
        assertTrue(isUserSignIn(clientDb.getSource()),
            "Unsupported generate token from client: " + clientDb.getSource());
        // Validate token name uniqueness
        authUserTokenQuery.checkNameNotExisted(userToken);
        // Validate user token quota
        authUserTokenQuery.checkTokenQuota(currentUserId, 1);
      }

      @Override
      protected AuthUserToken process() {
        // Cache user details for authentication provider
        daoAuthenticationProvider.getUserCache().putUserInCache(userDb.getId(),
            userDb.getUsername(), AuthUser.with(userDb));

        // Set custom access token attributes for OAuth2AccessTokenGenerator
        setRequestAttribute(CUSTOM_ACCESS_TOKEN, true);
        setRequestAttribute(CUSTOM_ACCESS_TOKEN_NAME, userToken.getName());
        // Set token expiration date if specified (null means permanent validity)
        if (nonNull(userToken.getExpiredDate())) {
          setRequestAttribute(ACCESS_TOKEN_EXPIRED_DATE, asInstant(userToken.getExpiredDate()));
        }

        // Submit OAuth2 authentication request for token generation
        Map<String, String> result;
        try {
          result = submitOauth2UserSignInRequest(clientDb.getClientId(),
              clientDb.getClientSecret(), SignInType.ACCOUNT_PASSWORD, userDb.getId(),
              userDb.getUsername(), userToken.getPassword(), USER_TOKEN_CLIENT_SCOPE);
        } catch (Throwable e) {
          if (e instanceof AbstractResultMessageException) {
            throw (AbstractResultMessageException) e;
          }
          throw new SysException(e.getMessage());
        }

        // Save user token with encrypted access token
        String userAccessToken = result.get(AuthKey.ACCESS_TOKEN);
        userToken.setDecryptedValue(userAccessToken);
        userToken.setValue(authUserTokenQuery.encryptValue(userAccessToken));
        userToken.setHash(hashToken(userAccessToken));
        userToken.setId(uidGenerator.getUID());
        insert0(userToken);

        // Record token creation audit log
        operationLogCmd.add(USER_TOKEN, userToken, CREATED);
        return userToken;
      }
    }.execute();
  }

  /**
   * Deletes user tokens and cleans up OAuth2 authorizations.
   *
   * <p>Note: Access tokens will automatically expire in OAuth2. After expiration,
   * the configuration needs to be manually deleted by the user.</p>
   *
   * <p>This method performs comprehensive cleanup including:</p>
   * <ul>
   *   <li>Finding user tokens by identifiers</li>
   *   <li>Removing OAuth2 authorizations</li>
   *   <li>Deleting token records</li>
   *   <li>Recording deletion audit logs</li>
   * </ul>
   *
   * @param ids Set of token identifiers to delete
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(Set<Long> ids) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        // Find user tokens for deletion
        List<AuthUserToken> userTokensDb = authUserTokenQuery.find0(ids);
        if (isEmpty(userTokensDb)) {
          return null;
        }

        // Remove OAuth2 authorizations for each token
        for (AuthUserToken userToken : userTokensDb) {
          String accessToken = authUserTokenQuery.decryptValue(userToken.getValue());
          OAuth2Authorization authorizationDb = oauth2AuthorizationService.findByToken(
              accessToken, null);
          if (nonNull(authorizationDb)) {
            oauth2AuthorizationService.remove(authorizationDb);
          }
        }

        // Delete token records from repository
        authUserTokenRepo.deleteByIdIn(ids);

        // Record token deletion audit logs
        operationLogCmd.addAll(USER_TOKEN, userTokensDb, DELETED);
        return null;
      }
    }.execute();
  }


  /**
   * 对令牌进行哈希处理
   */
  public static String hashToken(String token) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(token.getBytes());
      StringBuilder hexString = new StringBuilder();
      for (byte b : hash) {
        String hex = Integer.toHexString(0xff & b);
        if (hex.length() == 1) {
          hexString.append('0');
        }
        hexString.append(hex);
      }
      return hexString.toString();
    } catch (Exception e) {
      throw new RuntimeException("令牌哈希处理失败", e);
    }
  }

  @Override
  protected BaseRepository<AuthUserToken, Long> getRepository() {
    return authUserTokenRepo;
  }
}
