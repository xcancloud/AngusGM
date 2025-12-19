package cloud.xcan.angus.core.gm.application.cmd.auth.impl;

import static cloud.xcan.angus.api.commonlink.AuthConstant.CACHE_PASSWORD_ERROR_LOCKED_PREFIX;
import static cloud.xcan.angus.api.commonlink.AuthConstant.CACHE_PASSWORD_ERROR_NUM_PREFIX;
import static cloud.xcan.angus.api.commonlink.UCConstant.PASSWORD_ENCRYP_TYPE_SUFFIX;
import static cloud.xcan.angus.api.commonlink.UCConstant.PASSWORD_PROXY_ENCRYP;
import static cloud.xcan.angus.api.commonlink.UCConstant.PASSWORD_PROXY_ENCRYP_TYPE;
import static cloud.xcan.angus.api.commonlink.client.ClientSource.isUserSignIn;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.gm.application.converter.UserSignConverter.signupToAddUser;
import static cloud.xcan.angus.core.gm.domain.AuthMessage.LINK_SECRET_ILLEGAL;
import static cloud.xcan.angus.core.gm.domain.AuthMessage.LINK_SECRET_TIMEOUT;
import static cloud.xcan.angus.core.gm.domain.AuthMessage.SIGN_IN_ACCOUNT_EMPTY;
import static cloud.xcan.angus.core.gm.domain.AuthMessage.SIGN_IN_DEVICE_ID_EMPTY;
import static cloud.xcan.angus.core.gm.domain.AuthMessage.SIGN_IN_PASSWORD_ERROR_LOCKED_RETRY_CODE;
import static cloud.xcan.angus.core.gm.domain.AuthMessage.SIGN_IN_PASSWORD_ERROR_LOCKED_RETRY_T;
import static cloud.xcan.angus.core.gm.domain.AuthMessage.SIGN_IN_PASSWORD_ERROR_OVER_LIMIT_CODE;
import static cloud.xcan.angus.core.gm.domain.AuthMessage.SIGN_IN_PASSWORD_ERROR_OVER_LIMIT_T;
import static cloud.xcan.angus.core.gm.domain.AuthMessage.TOKEN_NOT_SING_IN_LOGOUT;
import static cloud.xcan.angus.core.gm.domain.AuthMessage.TOKEN_NOT_SING_IN_LOGOUT_CODE;
import static cloud.xcan.angus.core.gm.domain.operation.OperationResourceType.USER;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.SIGN_IN_FAIL;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.SIGN_IN_SUCCESS;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.SIGN_OUT;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.UPDATE_PASSWORD;
import static cloud.xcan.angus.core.utils.CoreUtils.calcPasswordStrength;
import static cloud.xcan.angus.core.utils.ValidatorUtils.checkMobile;
import static cloud.xcan.angus.remote.message.ProtocolException.M.CLIENT_NOT_FOUND;
import static cloud.xcan.angus.remote.message.ProtocolException.M.CLIENT_NOT_FOUND_KEY;
import static cloud.xcan.angus.remote.message.ProtocolException.M.PARAM_MISSING_KEY;
import static cloud.xcan.angus.remote.message.ProtocolException.M.QUERY_FIELD_EMPTY_T;
import static cloud.xcan.angus.security.authentication.password.OAuth2PasswordAuthenticationProviderUtils.DEFAULT_ENCODING_ID;
import static cloud.xcan.angus.spec.http.MediaType.APPLICATION_FORM_URLENCODED;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getRequestId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;
import static java.lang.Integer.parseInt;
import static java.lang.Long.valueOf;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import cloud.xcan.angus.api.commonlink.AuthConstant;
import cloud.xcan.angus.api.commonlink.authuser.AuthUser;
import cloud.xcan.angus.api.commonlink.authuser.AuthUserRepo;
import cloud.xcan.angus.api.commonlink.email.EmailBizKey;
import cloud.xcan.angus.api.commonlink.setting.security.SigninLimit;
import cloud.xcan.angus.api.commonlink.setting.tenant.SettingTenant;
import cloud.xcan.angus.api.commonlink.sms.SmsBizKey;
import cloud.xcan.angus.api.commonlink.user.SignupType;
import cloud.xcan.angus.api.enums.SignInType;
import cloud.xcan.angus.api.enums.UserSource;
import cloud.xcan.angus.core.biz.BizAssert;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.biz.exception.BizException;
import cloud.xcan.angus.core.gm.application.cmd.auth.AuthUserSignCmd;
import cloud.xcan.angus.core.gm.application.cmd.email.EmailCmd;
import cloud.xcan.angus.core.gm.application.cmd.operation.OperationLogCmd;
import cloud.xcan.angus.core.gm.application.cmd.sms.SmsCmd;
import cloud.xcan.angus.core.gm.application.cmd.user.UserCmd;
import cloud.xcan.angus.core.gm.application.query.auth.AuthClientQuery;
import cloud.xcan.angus.core.gm.application.query.auth.AuthUserQuery;
import cloud.xcan.angus.core.gm.application.query.auth.AuthUserSignQuery;
import cloud.xcan.angus.core.gm.domain.user.directory.UserDirectory;
import cloud.xcan.angus.core.gm.domain.user.directory.UserDirectoryRepo;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.spring.SpringContextHolder;
import cloud.xcan.angus.core.spring.boot.ApplicationInfo;
import cloud.xcan.angus.core.utils.ValidatorUtils;
import cloud.xcan.angus.lettucex.util.RedisService;
import cloud.xcan.angus.remote.message.AbstractResultMessageException;
import cloud.xcan.angus.remote.message.ProtocolException;
import cloud.xcan.angus.remote.message.SysException;
import cloud.xcan.angus.remote.message.http.Unauthorized;
import cloud.xcan.angus.security.authentication.dao.DaoAuthenticationProvider;
import cloud.xcan.angus.security.client.CustomOAuth2RegisteredClient;
import cloud.xcan.angus.spec.annotations.DoInFuture;
import cloud.xcan.angus.spec.experimental.BizConstant.Header;
import cloud.xcan.angus.spec.experimental.IdKey;
import cloud.xcan.angus.spec.http.HttpMethod;
import cloud.xcan.angus.spec.http.HttpSender;
import cloud.xcan.angus.spec.http.HttpSender.Request;
import cloud.xcan.angus.spec.http.HttpSender.Response;
import cloud.xcan.angus.spec.http.HttpStatus;
import cloud.xcan.angus.spec.http.HttpUrlConnectionSender;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import cloud.xcan.angus.spec.utils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.Resource;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of user sign-in command operations for managing user authentication.
 *
 * <p>This class provides comprehensive functionality for user authentication including:</p>
 * <ul>
 *   <li>User registration with verification codes</li>
 *   <li>User sign-in with multiple authentication types</li>
 *   <li>Token refresh and sign-out operations</li>
 *   <li>Password reset and forget password functionality</li>
 *   <li>Directory authentication integration</li>
 *   <li>Sign-in attempt limiting and security</li>
 * </ul>
 *
 * <p>The implementation supports various authentication methods including
 * mobile, email, and directory authentication with comprehensive security features.</p>
 */
@org.springframework.stereotype.Service
@Slf4j
public class AuthUserSignCmdImpl extends CommCmd<AuthUser, Long> implements AuthUserSignCmd {

  @Resource
  private AuthUserRepo authUserRepo;
  @Resource
  private AuthUserSignQuery authUserSignQuery;
  @Resource
  private SmsCmd smsCmd;
  @Resource
  private EmailCmd emailCmd;
  @Resource
  private AuthUserQuery authUserQuery;
  @Resource
  private UserDirectoryRepo userDirectoryRepo;
  @Resource
  private AuthClientQuery clientQuery;
  @Resource
  private UserCmd userCmd;
  @Resource
  private OperationLogCmd operationLogCmd;
  @Resource
  private RedisService<String> stringRedisService;
  @Resource
  private PasswordEncoder passwordEncoder;
  @Resource
  private OAuth2AuthorizationService oauth2AuthorizationService;
  @Resource
  private DaoAuthenticationProvider daoAuthenticationProvider;

  /**
   * Registers a new user with verification code validation.
   *
   * <p>This method performs user registration including:</p>
   * <ul>
   *   <li>Validating required parameters and verification codes</li>
   *   <li>Converting signup data to user format</li>
   *   <li>Creating user with appropriate source tracking</li>
   * </ul>
   *
   * @param user Authentication user entity with registration data
   * @return User identifier with associated data
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public IdKey<Long, Object> signup(AuthUser user) {
    return new BizTemplate<IdKey<Long, Object>>() {
      @Override
      protected void checkParams() {
        checkRequiredParamAndVerifyCode(user);
      }

      @Override
      protected IdKey<Long, Object> process() {
        // Determine user source based on invitation code
        UserSource userSource = isBlank(user.getInvitationCode()) ?
            UserSource.PLATFORM_SIGNUP : UserSource.INVITATION_CODE_SIGNUP;
        return userCmd.add(signupToAddUser(user, user.getInvitationCode()),
            null, null, null, userSource);
      }
    }.execute();
  }

  /**
   * Authenticates user with OAuth2 token generation.
   *
   * <p>This method performs comprehensive user authentication including:</p>
   * <ul>
   *   <li>Validating required parameters and client credentials</li>
   *   <li>Checking sign-in attempt limits and security</li>
   *   <li>Handling directory authentication integration</li>
   *   <li>Generating OAuth2 tokens</li>
   *   <li>Recording authentication audit logs</li>
   * </ul>
   *
   * @param clientId     OAuth2 client identifier
   * @param clientSecret OAuth2 client secret
   * @param signinType   Type of sign-in (mobile, email, etc.)
   * @param userId       User identifier (optional)
   * @param account      User account (username, mobile, email)
   * @param password     User password
   * @param scope        Requested OAuth2 scope
   * @param deviceId     Device identifier for tracking
   * @return Map containing OAuth2 tokens and response data
   */
  @Override
  public Map<String, String> signin(String clientId, String clientSecret, SignInType signinType,
      @Nullable Long userId, String account, String password, String scope, String deviceId) {
    return new BizTemplate<Map<String, String>>(false) {
      AuthUser userDb;

      @Override
      protected void checkParams() {
        // Validate required account parameters
        checkRequiredParameters(userId, account, deviceId);
        // Validate client credentials and scope
        clientQuery.checkAndFind(clientId, clientSecret, scope);
        // Validate user account exists and credentials are correct
        userDb = authUserQuery.checkAndFindByAccount(userId, signinType, account, password);
      }

      @Override
      protected Map<String, String> process() {
        try {
          // Set login user principal context
          PrincipalContext.get().setClientId(clientId)
              .setUserId(valueOf(userDb.getId())).setFullName(userDb.getFullName())
              .setTenantId(valueOf(userDb.getTenantId()))
              .setTenantName(userDb.getTenantName());

          // Check sign-in attempt limits and security
          checkSignInPasswordErrorNum(userDb.getTenantId(), userDb.getUsername());

          // Cache user directory for LDAP authentication
          cacheUserDirectory(userDb);

          // Cache user details for authentication provider
          daoAuthenticationProvider.getUserCache().putUserInCache(userDb.getId(),
              userDb.getUsername(), AuthUser.with(userDb));

          // Submit OAuth2 authentication request
          Map<String, String> result = submitOauth2UserSignInRequest(clientId, clientSecret,
              signinType, userDb.getId(), account, password, scope);

          // Update directory password after successful authentication
          updateNewDirectoryPassword(userDb, password);

          // Record successful sign-in audit log
          operationLogCmd.add(USER, userDb, SIGN_IN_SUCCESS);
          return result;
        } catch (Throwable e) {
          // Record failed sign-in attempt
          recordSignInPasswordErrorNum(valueOf(userDb.getTenantId()), userDb.getUsername());
          // Record failed sign-in audit log
          operationLogCmd.add(USER, userDb, SIGN_IN_FAIL, e.getMessage());

          if (e instanceof AbstractResultMessageException) {
            throw (AbstractResultMessageException) e;
          }
          throw new SysException(e.getMessage());
        }
      }
    }.execute();
  }

  /**
   * Refreshes OAuth2 access token using refresh token.
   *
   * <p>This method handles token refresh including:</p>
   * <ul>
   *   <li>Validating client credentials</li>
   *   <li>Submitting refresh token request</li>
   *   <li>Returning new access token</li>
   * </ul>
   *
   * @param clientId     OAuth2 client identifier
   * @param clientSecret OAuth2 client secret
   * @param refreshToken Refresh token for token renewal
   * @return Map containing new OAuth2 tokens
   */
  @Override
  public Map<String, String> renew(String clientId, String clientSecret, String refreshToken) {
    return new BizTemplate<Map<String, String>>(false) {
      @Override
      protected void checkParams() {
        // Validate client credentials
        clientQuery.checkAndFind(clientId, clientSecret);
      }

      @Override
      protected Map<String, String> process() {
        // Submit OAuth2 refresh token request
        try {
          return submitOauth2RenewRequest(clientId, clientSecret, refreshToken);
        } catch (Throwable e) {
          throw new SysException(e.getMessage());
        }
      }
    }.execute();
  }

  /**
   * Signs out user by invalidating OAuth2 authorization.
   *
   * <p>This method handles user sign-out including:</p>
   * <ul>
   *   <li>Validating client credentials</li>
   *   <li>Finding and removing OAuth2 authorization</li>
   *   <li>Recording sign-out audit logs</li>
   * </ul>
   *
   * @param clientId     OAuth2 client identifier
   * @param clientSecret OAuth2 client secret
   * @param accessToken  Access token to invalidate
   */
  @Override
  public void signout(String clientId, String clientSecret, String accessToken) {
    new BizTemplate<Void>() {
      OAuth2Authorization authorizationDb;

      @Override
      protected void checkParams() {
        // Validate client credentials
        CustomOAuth2RegisteredClient clientDb = clientQuery.checkAndFind(clientId, clientSecret);

        // Find existing authorization by token
        authorizationDb = oauth2AuthorizationService.findByToken(accessToken, null);

        if (nonNull(authorizationDb)) {
          // Validate client ID consistency
          assertTrue(clientDb.getId().equals(authorizationDb.getRegisteredClientId()),
              CLIENT_NOT_FOUND, CLIENT_NOT_FOUND_KEY, null);

          // Ensure only user sign-in tokens can be logged out
          assertTrue(isUserSignIn(clientDb.getSource()),
              TOKEN_NOT_SING_IN_LOGOUT_CODE, TOKEN_NOT_SING_IN_LOGOUT, null);
        }
      }

      @Override
      protected Void process() {
        if (isNull(authorizationDb)) {
          return null;
        }
        // Remove OAuth2 authorization
        oauth2AuthorizationService.remove(authorizationDb);

        // Record sign-out audit log
        AuthUser userDb = authUserQuery.findByUsername(authorizationDb.getPrincipalName());
        PrincipalContext.get().setClientId(clientId)
            .setUserId(valueOf(userDb.getId())).setFullName(userDb.getFullName())
            .setTenantId(valueOf(userDb.getTenantId())).setTenantName(userDb.getTenantName());
        operationLogCmd.add(USER, userDb, SIGN_OUT);
        return null;
      }
    }.execute();
  }

  /**
   * Resets user password using verification link secret.
   *
   * <p>This method handles password reset including:</p>
   * <ul>
   *   <li>Validating user existence and status</li>
   *   <li>Verifying link secret from email/SMS</li>
   *   <li>Updating password with security settings</li>
   *   <li>Recording password update audit logs</li>
   * </ul>
   *
   * @param userId      User identifier
   * @param newPassword New password to set
   * @param linkSecret  Verification link secret from email/SMS
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void forgetPassword(Long userId, String newPassword, String linkSecret) {
    new BizTemplate<Void>() {
      AuthUser userDb = null;

      @Override
      protected void checkParams() {
        // Validate user exists
        userDb = authUserQuery.checkAndFind(userId);

        // Validate verification link secret
        checkForgetPasswordLinkSecret(userId, linkSecret);

        // Validate user status
        authUserQuery.checkUserValid(userDb);

        // Validate password length requirements
        authUserSignQuery.checkMinPasswordLengthByTenantSetting(
            valueOf(userDb.getTenantId()), newPassword);
      }

      @Override
      protected Void process() {
        // Update password with security settings
        userDb.setPasswordStrength(calcPasswordStrength(newPassword).getValue());
        userDb.setPassword(passwordEncoder.encode(newPassword));
        userDb.setLastModifiedPasswordDate(Instant.now());
        authUserRepo.save(userDb);

        // Record password update audit log
        PrincipalContext.get().setUserId(valueOf(userDb.getId()))
            .setFullName(userDb.getFullName()).setTenantId(valueOf(userDb.getTenantId()))
            .setTenantName(userDb.getTenantName());
        operationLogCmd.add(USER, userDb, UPDATE_PASSWORD);
        return null;
      }
    }.execute();
  }

  /**
   * Updates directory password after successful directory authentication.
   *
   * <p>This method handles directory password updates when users successfully
   * authenticate through directory services like LDAP.</p>
   *
   * @param user     Authentication user entity
   * @param password New password from directory authentication
   */
  private void updateNewDirectoryPassword(AuthUser user, String password) {
    Object ldapProxyPassword = PrincipalContext.getExtension("ldapProxyPassword");
    if (nonNull(ldapProxyPassword) && nonNull(user.getDirectoryId())
        && ldapProxyPassword.toString().startsWith(PASSWORD_PROXY_ENCRYP)) {
      String[] newPasswordEncrypt = passwordEncoder.encode(password)
          .split(PASSWORD_ENCRYP_TYPE_SUFFIX);
      user.setPassword(PASSWORD_PROXY_ENCRYP + newPasswordEncrypt[1]);
      authUserRepo.save(user);
    }
  }

  /**
   * Caches user directory information for LDAP authentication.
   *
   * <p>This method prepares user directory information for use by
   * LDAP authentication providers.</p>
   *
   * @param user Authentication user entity
   */
  private void cacheUserDirectory(@Nullable AuthUser user) {
    if (nonNull(user) && user.supportDirectoryAuth()) {
      UserDirectory userDirectory = userDirectoryRepo.findById(valueOf(user.getDirectoryId()))
          .orElse(null);
      if (nonNull(userDirectory) && nonNull(userDirectory.getEnabled())
          && userDirectory.getEnabled()) {
        PrincipalContext.addExtension("userDirectory", userDirectory);
        PrincipalContext.addExtension("fullName", user.getFullName()); // LDAP -> CN
        PrincipalContext.addExtension("ldapProxyPassword", user.getPassword());
      }
    }
  }

  /**
   * Cancels directory authentication when directory is deleted.
   *
   * <p>Note: This method will be enhanced to replace bcrypt password after
   * directory deletion and cancel authentication from directory.</p>
   *
   * @param user Authentication user entity
   */
  @DoInFuture("Replace bcrypt password after directory be deleted and cancel auth from the directory.")
  public void cancelDirectoryAuth(AuthUser user) {
    if (nonNull(user.getPassword()) && user.getPassword().startsWith(PASSWORD_PROXY_ENCRYP)
        && isNull(user.getDirectoryId()) /* user directory deleted  */) {
      user.setPassword(
          user.getPassword().replaceFirst(PASSWORD_PROXY_ENCRYP_TYPE, DEFAULT_ENCODING_ID));
      authUserRepo.save(user);
    }
  }

  /**
   * Submits OAuth2 user sign-in request to authorization server.
   *
   * <p>This method constructs and sends OAuth2 password grant request
   * to the authorization server for token generation.</p>
   *
   * @param clientId     OAuth2 client identifier
   * @param clientSecret OAuth2 client secret
   * @param signinType   Type of sign-in authentication
   * @param userId       User identifier
   * @param account      User account
   * @param password     User password
   * @param scope        Requested OAuth2 scope
   * @return Map containing OAuth2 response data
   * @throws Throwable if authentication request fails
   */
  public static Map<String, String> submitOauth2UserSignInRequest(String clientId,
      String clientSecret, SignInType signinType, String userId, String account, String password,
      String scope) throws Throwable {
    // Construct OAuth2 password grant request
    String authContent = format(
        "client_id=%s&client_secret=%s&grant_type=%s&user_id=%s&account=%s&password=%s",
        clientId, clientSecret, signinType.toOAuth2GrantType(), userId, account, password);
    if (isNotEmpty(scope)) {
      authContent = authContent + "&scope=" + scope;
    }
    return sendOauth2Request(authContent);
  }

  /**
   * Submits OAuth2 refresh token request to authorization server.
   *
   * @param clientId     OAuth2 client identifier
   * @param clientSecret OAuth2 client secret
   * @param refreshToken Refresh token for renewal
   * @return Map containing OAuth2 response data
   * @throws Throwable if refresh request fails
   */
  private Map<String, String> submitOauth2RenewRequest(String clientId, String clientSecret,
      String refreshToken) throws Throwable {
    // Construct OAuth2 refresh token request
    String authContent = format("client_id=%s&client_secret=%s&grant_type=%s&refresh_token=%s",
        clientId, clientSecret, AuthorizationGrantType.REFRESH_TOKEN.getValue(), refreshToken);
    return sendOauth2Request(authContent);
  }

  /**
   * Sends OAuth2 request to authorization server.
   *
   * <p>This method handles HTTP communication with the OAuth2 authorization server
   * and processes the response for token generation.</p>
   *
   * @param authContent OAuth2 request content
   * @return Map containing OAuth2 response data
   * @throws Throwable if request fails
   */
  public static Map<String, String> sendOauth2Request(String authContent) throws Throwable {
    HttpSender sender = new HttpUrlConnectionSender();
    ApplicationInfo applicationInfo = SpringContextHolder.getBean(ApplicationInfo.class);
    String tokenEndpoint = format("http://%s/oauth2/token", applicationInfo.getInstanceId());
    Response response = Request.build(tokenEndpoint, sender)
        .withMethod(HttpMethod.POST).withHeader(Header.REQUEST_ID, getRequestId())
        .withContent(APPLICATION_FORM_URLENCODED, authContent).send();
    Map<String, String> result = JsonUtils.convert(response.body(), new TypeReference<>() {
    });
    if (!response.isSuccessful()) {
      assert result != null;
      if (response.code() == HttpStatus.UNAUTHORIZED.value) {
        throw Unauthorized.of(nullSafe(result.get("error_description"), result.get("error")));
      } else {
        throw ProtocolException.of(nullSafe(result.get("error_description"), result.get("error")));
      }
    }
    return result;
  }

  /**
   * Validates required parameters for sign-in operation.
   *
   * @param userId   User identifier (optional)
   * @param account  User account
   * @param deviceId Device identifier
   */
  private void checkRequiredParameters(Long userId, String account, String deviceId) {
    assertTrue(nonNull(userId) || nonNull(account), SIGN_IN_ACCOUNT_EMPTY, PARAM_MISSING_KEY);
    assertTrue(isNotEmpty(deviceId), SIGN_IN_DEVICE_ID_EMPTY, PARAM_MISSING_KEY);
  }

  /**
   * Validates required parameters and verification codes for signup.
   *
   * @param user Authentication user entity with signup data
   */
  private void checkRequiredParamAndVerifyCode(AuthUser user) {
    String mobile = user.getMobile(), email = user.getEmail(), country = user.getCountry();
    if (user.getSignupType().equals(SignupType.MOBILE.getValue())) {
      assertTrue(isNotBlank(mobile), QUERY_FIELD_EMPTY_T, new Object[]{"mobile"});
      assertTrue(isNotBlank(country), QUERY_FIELD_EMPTY_T, new Object[]{"country"});
      checkMobile(user.getCountry(), mobile);
      smsCmd.checkVerificationCode(SmsBizKey.valueOf(user.getSmsBizKey()),
          mobile, user.getVerificationCode());
    }

    if (user.getSignupType().equals(SignupType.EMAIL.getValue())) {
      assertTrue(isNotBlank(email), QUERY_FIELD_EMPTY_T, new Object[]{"email"});
      ValidatorUtils.checkEmail(email);
      emailCmd.checkVerificationCode(EmailBizKey.valueOf(user.getEmailBizKey()),
          email, user.getVerificationCode());
    }
  }

  /**
   * Validates forget password link secret from email/SMS.
   *
   * @param userId     User identifier
   * @param linkSecret Link secret to validate
   */
  private void checkForgetPasswordLinkSecret(Long userId, String linkSecret) {
    String emailCacheKey = format(AuthConstant.CACHE_EMAIL_CHECK_SECRET_PREFIX,
        EmailBizKey.PASSWORD_FORGET, userId);
    String smsCacheKey = format(AuthConstant.CACHE_SMS_CHECK_SECRET_PREFIX,
        SmsBizKey.PASSWORD_FORGET, userId);
    String emailLinkSecret = stringRedisService.get(emailCacheKey);
    String smsLinkSecret = stringRedisService.get(smsCacheKey);
    assertTrue(isNotBlank(emailLinkSecret) || isNotBlank(smsLinkSecret), LINK_SECRET_TIMEOUT);
    assertTrue(StringUtils.equals(smsLinkSecret, linkSecret)
        || StringUtils.equalsIgnoreCase(emailLinkSecret, linkSecret), LINK_SECRET_ILLEGAL);
    stringRedisService.delete(emailCacheKey);
    stringRedisService.delete(smsCacheKey);
  }

  /**
   * Checks sign-in password error limits and security settings.
   *
   * @param tenantId     Tenant identifier
   * @param finalAccount User account for error tracking
   */
  private void checkSignInPasswordErrorNum(String tenantId, String finalAccount) {
    String passwordLockedCacheKey = format(CACHE_PASSWORD_ERROR_LOCKED_PREFIX, finalAccount);
    String passwordLockedMinutes = stringRedisService.get(passwordLockedCacheKey);
    BizAssert.assertTrue(isNull(passwordLockedMinutes), SIGN_IN_PASSWORD_ERROR_LOCKED_RETRY_CODE,
        SIGN_IN_PASSWORD_ERROR_LOCKED_RETRY_T, new Object[]{passwordLockedMinutes});

    String passwordErrorNumCacheKey = format(CACHE_PASSWORD_ERROR_NUM_PREFIX, finalAccount);
    String passwordErrorNum = stringRedisService.get(passwordErrorNumCacheKey);
    if (isNull(passwordErrorNum)) {
      return;
    }

    // Check tenant sign-in limit configuration
    SettingTenant settingTenant = authUserSignQuery.checkAndFindSettingTenant(valueOf(tenantId));
    if (isNull(settingTenant) || isNull(settingTenant.getSecurityData())) {
      return;
    }

    // Check if sign-in limit is enabled
    SigninLimit signinLimit = settingTenant.getSecurityData().getSigninLimit();
    if (nonNull(signinLimit) && signinLimit.getEnabled()) {
      int errorCount = Integer.parseInt(passwordErrorNum);
      if (errorCount >= signinLimit.getLockedPasswordErrorNum()) {
        stringRedisService.set(passwordLockedCacheKey, passwordErrorNum,
            signinLimit.getPasswordErrorIntervalInMinutes(), TimeUnit.MINUTES);
        stringRedisService.delete(passwordErrorNumCacheKey);

        throw BizException.of(SIGN_IN_PASSWORD_ERROR_OVER_LIMIT_CODE,
            SIGN_IN_PASSWORD_ERROR_OVER_LIMIT_T, new Object[]{passwordErrorNum});
      }
    }
  }

  /**
   * Records sign-in password error attempts for security tracking.
   *
   * @param tenantId     Tenant identifier
   * @param innerAccount User account for error tracking
   */
  public void recordSignInPasswordErrorNum(Long tenantId, String innerAccount) {
    String passwordLockedCacheKey = format(CACHE_PASSWORD_ERROR_LOCKED_PREFIX, innerAccount);
    String passwordLockedMinutes = stringRedisService.get(passwordLockedCacheKey);
    if (Objects.nonNull(passwordLockedMinutes)) {
      // Do not record errors after account is locked
      return;
    }
    String passwordErrorNumCacheKey = format(CACHE_PASSWORD_ERROR_NUM_PREFIX, innerAccount);
    String passwordErrorNum = stringRedisService.get(passwordErrorNumCacheKey);
    if (Objects.nonNull(passwordErrorNum)) {
      passwordErrorNum = String.valueOf(parseInt(passwordErrorNum) + 1);
    } else {
      passwordErrorNum = String.valueOf(1);
    }
    stringRedisService.set(passwordErrorNumCacheKey, passwordErrorNum);

    // Check tenant sign-in limit configuration
    SettingTenant settingTenant = authUserSignQuery.checkAndFindSettingTenant(tenantId);
    if (isNull(settingTenant) || isNull(settingTenant.getSecurityData())) {
      return;
    }

    // Check if sign-in limit is enabled and lock account if needed
    SigninLimit signinLimit = settingTenant.getSecurityData().getSigninLimit();
    if (nonNull(signinLimit) && signinLimit.getEnabled()
        && Integer.parseInt(passwordErrorNum) >= signinLimit.getLockedPasswordErrorNum()) {
      stringRedisService.set(passwordLockedCacheKey, passwordErrorNum,
          signinLimit.getPasswordErrorIntervalInMinutes(), TimeUnit.MINUTES);
      stringRedisService.delete(passwordErrorNumCacheKey);
    }
  }

  @Override
  protected BaseRepository<AuthUser, Long> getRepository() {
    return this.authUserRepo;
  }
}
