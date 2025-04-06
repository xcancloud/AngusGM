package cloud.xcan.angus.core.gm.application.cmd.authuser.impl;

import static cloud.xcan.angus.api.commonlink.AASConstant.CACHE_PASSWORD_ERROR_LOCKED_PREFIX;
import static cloud.xcan.angus.api.commonlink.AASConstant.CACHE_PASSWORD_ERROR_NUM_PREFIX;
import static cloud.xcan.angus.api.commonlink.UCConstant.PASSWORD_ENCRYP_TYPE_SUFFIX;
import static cloud.xcan.angus.api.commonlink.UCConstant.PASSWORD_PROXY_ENCRYP;
import static cloud.xcan.angus.api.commonlink.UCConstant.PASSWORD_PROXY_ENCRYP_TYPE;
import static cloud.xcan.angus.api.commonlink.client.ClientSource.isUserSignIn;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.gm.application.converter.UserSignConverter.convertClientSuccessAuthentication;
import static cloud.xcan.angus.core.gm.application.converter.UserSignConverter.convertUserRenewAuthentication;
import static cloud.xcan.angus.core.gm.application.converter.UserSignConverter.convertUserSignInAuthentication;
import static cloud.xcan.angus.core.gm.application.converter.UserSignConverter.getSignoutOperationEvent;
import static cloud.xcan.angus.core.gm.application.converter.UserSignConverter.getSignupOperationEvent;
import static cloud.xcan.angus.core.gm.application.converter.UserSignConverter.signupToAddUser;
import static cloud.xcan.angus.core.gm.domain.AASCoreMessage.LINK_SECRET_ILLEGAL;
import static cloud.xcan.angus.core.gm.domain.AASCoreMessage.LINK_SECRET_TIMEOUT;
import static cloud.xcan.angus.core.gm.domain.AASCoreMessage.SIGN_IN_ACCOUNT_EMPTY;
import static cloud.xcan.angus.core.gm.domain.AASCoreMessage.SIGN_IN_DEVICE_ID_EMPTY;
import static cloud.xcan.angus.core.gm.domain.AASCoreMessage.SIGN_IN_PASSWORD_ERROR_LOCKED_RETRY_CODE;
import static cloud.xcan.angus.core.gm.domain.AASCoreMessage.SIGN_IN_PASSWORD_ERROR_LOCKED_RETRY_T;
import static cloud.xcan.angus.core.gm.domain.AASCoreMessage.SIGN_IN_PASSWORD_ERROR_OVER_LIMIT_CODE;
import static cloud.xcan.angus.core.gm.domain.AASCoreMessage.SIGN_IN_PASSWORD_ERROR_OVER_LIMIT_T;
import static cloud.xcan.angus.core.gm.domain.AASCoreMessage.TOKEN_NOT_SING_IN_LOGOUT;
import static cloud.xcan.angus.core.gm.domain.AASCoreMessage.TOKEN_NOT_SING_IN_LOGOUT_CODE;
import static cloud.xcan.angus.core.utils.CoreUtils.calcPasswordStrength;
import static cloud.xcan.angus.core.utils.ValidatorUtils.checkMobile;
import static cloud.xcan.angus.remote.message.CommProtocolException.M.CLIENT_NOT_FOUND;
import static cloud.xcan.angus.remote.message.CommProtocolException.M.CLIENT_NOT_FOUND_KEY;
import static cloud.xcan.angus.remote.message.CommProtocolException.M.PARAM_MISSING_KEY;
import static cloud.xcan.angus.remote.message.CommProtocolException.M.QUERY_FIELD_EMPTY_T;
import static cloud.xcan.angus.security.authentication.password.OAuth2PasswordAuthenticationProviderUtils.DEFAULT_ENCODING_ID;
import static java.lang.Integer.parseInt;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import cloud.xcan.angus.api.commonlink.AASConstant;
import cloud.xcan.angus.api.commonlink.authuser.AuthUser;
import cloud.xcan.angus.api.commonlink.authuser.AuthUserRepo;
import cloud.xcan.angus.api.commonlink.email.EmailBizKey;
import cloud.xcan.angus.api.commonlink.setting.security.SigninLimit;
import cloud.xcan.angus.api.commonlink.setting.tenant.SettingTenant;
import cloud.xcan.angus.api.commonlink.sms.SmsBizKey;
import cloud.xcan.angus.api.commonlink.user.SignupType;
import cloud.xcan.angus.api.enums.SignInType;
import cloud.xcan.angus.api.enums.UserSource;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizAssert;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.biz.exception.BizException;
import cloud.xcan.angus.core.disruptor.DisruptorQueueManager;
import cloud.xcan.angus.core.event.OperationEvent;
import cloud.xcan.angus.core.gm.application.cmd.authuser.AuthUserSignCmd;
import cloud.xcan.angus.core.gm.application.cmd.email.EmailCmd;
import cloud.xcan.angus.core.gm.application.cmd.sms.SmsCmd;
import cloud.xcan.angus.core.gm.application.cmd.user.UserCmd;
import cloud.xcan.angus.core.gm.application.query.authuser.AuthUserQuery;
import cloud.xcan.angus.core.gm.application.query.authuser.AuthUserSignQuery;
import cloud.xcan.angus.core.gm.application.query.client.ClientQuery;
import cloud.xcan.angus.core.gm.domain.user.directory.UserDirectory;
import cloud.xcan.angus.core.gm.domain.user.directory.UserDirectoryRepo;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.utils.ValidatorUtils;
import cloud.xcan.angus.lettucex.util.RedisService;
import cloud.xcan.angus.security.authentication.dao.DaoAuthenticationProvider;
import cloud.xcan.angus.security.client.CustomOAuth2RegisteredClient;
import cloud.xcan.angus.spec.annotations.DoInFuture;
import cloud.xcan.angus.spec.experimental.IdKey;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import jakarta.annotation.Resource;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;


@Biz
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
  private ClientQuery clientQuery;

  @Resource
  private UserCmd userCmd;

  // TODO Use eventCmd.add()
  @Resource
  @Qualifier("operationEventDisruptorQueue")
  private DisruptorQueueManager<OperationEvent> operationEventQueue;

  @Resource
  private RedisService<String> stringRedisService;

  @Resource
  private PasswordEncoder passwordEncoder;

  @Resource
  private AuthenticationManager authenticationManager;

  @Resource
  private OAuth2AuthorizationService oauth2AuthorizationService;

  @Resource
  private DaoAuthenticationProvider daoAuthenticationProvider;

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
        UserSource userSource = isBlank(user.getInvitationCode()) ?
            UserSource.PLATFORM_SIGNUP : UserSource.INVITATION_CODE_SIGNUP;
        return userCmd.add(signupToAddUser(user, user.getInvitationCode()),
            null, null, null, userSource);
      }
    }.execute();
  }

  @Override
  public OAuth2AccessTokenAuthenticationToken signin(String clientId, String clientSecret,
      Set<String> scopes, SignInType signinType, @Nullable Long userId, String account,
      String password, String deviceId) {
    return new BizTemplate<OAuth2AccessTokenAuthenticationToken>(false) {
      CustomOAuth2RegisteredClient clientDb;
      AuthUser userDb;
      String finalAccount;

      @Override
      protected void checkParams() {
        // Check the required account parameters
        checkRequiredParameters(userId, account, deviceId);
        // Check the clientId, clientSecret and scopes are correct
        clientDb = clientQuery.checkAndFind(clientId, clientSecret, scopes);
        // Check and find the existed account
        userDb = authUserQuery.checkAndFindByAccount(userId, signinType, account, password);
        finalAccount = isNull(userDb) ? account : userDb.getUsername();
        // Check the number of password errors
        checkSignInPasswordErrorNum(Long.valueOf(userDb.getTenantId()), finalAccount);
      }

      @Override
      protected OAuth2AccessTokenAuthenticationToken process() {
        try {
          // Cached to context for LdapPasswordConnection login
          cacheUserDirectory(userDb);

          // Cached to context for load UserDetail
          daoAuthenticationProvider.getUserCache().putUserInCache(userDb.getId(),
              finalAccount, userDb);

          // Submit OAuth2 login authentication
          OAuth2ClientAuthenticationToken clientAuthenticationToken
              = convertClientSuccessAuthentication(clientDb);
          Authentication userAuthenticationToken = convertUserSignInAuthentication(
              signinType, userId, finalAccount, password, scopes, clientAuthenticationToken);
          OAuth2AccessTokenAuthenticationToken result = (OAuth2AccessTokenAuthenticationToken)
              authenticationManager.authenticate(userAuthenticationToken);

          // Save new bcrypt password after login directory success
          updateNewDirectoryPassword(userDb, password);

          // Record successful login logs
          if (nonNull(operationEventQueue)) {
            operationEventQueue.add(getSignupOperationEvent(clientId, userDb));
          }

          return result;
        } catch (Exception e) {
          if (nonNull(userDb)) {
            recordSignInPasswordErrorNum(Long.valueOf(userDb.getTenantId()), finalAccount);
          }
          throw e;
        }
      }
    }.execute();
  }

  @Override
  public OAuth2AccessTokenAuthenticationToken renew(String clientId, String clientSecret,
      String refreshToken, Set<String> scopes) {
    return new BizTemplate<OAuth2AccessTokenAuthenticationToken>(false) {
      CustomOAuth2RegisteredClient clientDb;

      @Override
      protected void checkParams() {
        // Check the clientId, clientSecret and scopes are correct
        clientDb = clientQuery.checkAndFind(clientId, clientSecret, scopes);
      }

      @Override
      protected OAuth2AccessTokenAuthenticationToken process() {
        // Submit OAuth2 refresh token authentication
        OAuth2ClientAuthenticationToken clientAuthenticationToken
            = convertClientSuccessAuthentication(clientDb);
        Authentication userAuthenticationToken = convertUserRenewAuthentication(
            refreshToken, scopes, clientAuthenticationToken);
        return (OAuth2AccessTokenAuthenticationToken)
            authenticationManager.authenticate(userAuthenticationToken);
      }
    }.execute();
  }

  @Override
  public void signout(String clientId, String clientSecret, String accessToken) {
    new BizTemplate<Void>() {
      OAuth2Authorization authorizationDb;

      @Override
      protected void checkParams() {
        // Check the clientId and clientSecret are correct
        CustomOAuth2RegisteredClient clientDb = clientQuery.checkAndFind(clientId, clientSecret);

        // Find the existed authorization
        authorizationDb = oauth2AuthorizationService.findByToken(accessToken, null);

        if (nonNull(authorizationDb)) {
          // Check the clientId is consistent
          assertTrue(clientId.equals(authorizationDb.getRegisteredClientId()),
              CLIENT_NOT_FOUND, CLIENT_NOT_FOUND_KEY, null);

          // Check cannot log out the user created token, and can only delete it manually
          assertTrue(isUserSignIn(clientDb.getSource()),
              TOKEN_NOT_SING_IN_LOGOUT_CODE, TOKEN_NOT_SING_IN_LOGOUT, null);
        }
      }

      @Override
      protected Void process() {
        if (isNull(authorizationDb)) {
          return null;
        }

        oauth2AuthorizationService.remove(authorizationDb);

        AuthUser user = authUserQuery.findByUsername(authorizationDb.getPrincipalName());
        operationEventQueue.add(getSignoutOperationEvent(clientId, user));
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void forgetPassword(Long userId, String newPassword, String linkSecret) {
    new BizTemplate<Void>() {
      AuthUser userDb = null;

      @Override
      protected void checkParams() {
        // Check and find existed user
        userDb = authUserQuery.checkAndFind(userId);

        // Check the verification code LinkSecret is valid
        checkForgetPasswordLinkSecret(userId, linkSecret);

        // Check the user status
        authUserQuery.checkUserValid(userDb);

        // Check the password length
        authUserSignQuery.checkMinPassdLengthByTenantSetting(
            Long.valueOf(userDb.getTenantId()), newPassword);
      }

      @Override
      protected Void process() {
        userDb.setPasswordStrength(calcPasswordStrength(newPassword).getValue());
        userDb.setPassword(passwordEncoder.encode(newPassword));
        userDb.setLastModifiedPasswordDate(Instant.now());
        authUserRepo.save(userDb);
        return null;
      }
    }.execute();
  }

  /**
   * Save new bcrypt password after login directory success
   *
   * @see AuthUserSignCmdImpl#cancelDirectoryAuth(AuthUser)
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
   * Cached to context for {@link `LdapPasswordConnection`}.
   */
  private void cacheUserDirectory(@Nullable AuthUser user) {
    if (nonNull(user) && user.supportDirectoryAuth()) {
      UserDirectory userDirectory = userDirectoryRepo.findById(
              Long.valueOf(user.getDirectoryId()))
          .orElse(null);
      if (nonNull(userDirectory) && nonNull(userDirectory.getEnabled())
          && userDirectory.getEnabled()) {
        PrincipalContext.addExtension("userDirectory", userDirectory);
        PrincipalContext.addExtension("fullname", user.getFullName()); // LDAP -> CN
        PrincipalContext.addExtension("ldapProxyPassword", user.getPassword());
      }
    }
  }

  @DoInFuture("Replace bcrypt password after directory be deleted and cancel auth from the directory.")
  public void cancelDirectoryAuth(AuthUser user) {
    if (nonNull(user.getPassword()) && user.getPassword().startsWith(PASSWORD_PROXY_ENCRYP)
        && isNull(user.getDirectoryId()) /* user directory deleted  */) {
      user.setPassword(
          user.getPassword().replaceFirst(PASSWORD_PROXY_ENCRYP_TYPE, DEFAULT_ENCODING_ID));
      authUserRepo.save(user);
    }
  }

  private void checkRequiredParameters(Long userId, String account, String deviceId) {
    assertTrue(nonNull(userId) || nonNull(account), SIGN_IN_ACCOUNT_EMPTY, PARAM_MISSING_KEY);
    assertTrue(isNotEmpty(deviceId), SIGN_IN_DEVICE_ID_EMPTY, PARAM_MISSING_KEY);
  }

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

  private void checkForgetPasswordLinkSecret(Long userId, String linkSecret) {
    String emailCacheKey = String.format(AASConstant.CACHE_EMAIL_CHECK_SECRET_PREFIX,
        EmailBizKey.PASSD_FORGET, userId);
    String smsCacheKey = String.format(AASConstant.CACHE_SMS_CHECK_SECRET_PREFIX,
        SmsBizKey.PASSD_FORGET, userId);
    String emailLinkSecret = stringRedisService.get(emailCacheKey);
    String smsLinkSecret = stringRedisService.get(smsCacheKey);
    assertTrue(isNotBlank(emailLinkSecret) || isNotBlank(smsLinkSecret), LINK_SECRET_TIMEOUT);
    assertTrue(StringUtils.equals(smsLinkSecret, linkSecret)
        || StringUtils.equalsIgnoreCase(emailLinkSecret, linkSecret), LINK_SECRET_ILLEGAL);
    stringRedisService.delete(emailCacheKey);
    stringRedisService.delete(smsCacheKey);
  }

  private void checkSignInPasswordErrorNum(Long tenantId, String finalAccount) {
    String passwordLockedCacheKey = String.format(CACHE_PASSWORD_ERROR_LOCKED_PREFIX, finalAccount);
    String passwordLockedMinutes = stringRedisService.get(passwordLockedCacheKey);
    BizAssert.assertTrue(isNull(passwordLockedMinutes), SIGN_IN_PASSWORD_ERROR_LOCKED_RETRY_CODE,
        SIGN_IN_PASSWORD_ERROR_LOCKED_RETRY_T, new Object[]{passwordLockedMinutes});

    String passwordErrorNumCacheKey = String.format(CACHE_PASSWORD_ERROR_NUM_PREFIX, finalAccount);
    String passwordErrorNum = stringRedisService.get(passwordErrorNumCacheKey);
    if (nonNull(passwordErrorNum)) {
      SettingTenant settingTenant = authUserSignQuery.checkAndFindSettingTenant(tenantId);
      SigninLimit signinLimit = settingTenant.getSecurityData().getSigninLimit();
      // When sign-in limit is enabled
      if (signinLimit.getEnabled()) {
        Integer errorNum = signinLimit.getLockedPassdErrorNum();
        int lockedDurationInMinutes = signinLimit.getLockedDurationInMinutes();
        if (parseInt(passwordErrorNum) >= errorNum) {
          stringRedisService.set(passwordLockedCacheKey, String.valueOf(lockedDurationInMinutes),
              lockedDurationInMinutes, TimeUnit.MINUTES);
          stringRedisService.delete(passwordErrorNumCacheKey);
          throw BizException.of(SIGN_IN_PASSWORD_ERROR_OVER_LIMIT_CODE,
              SIGN_IN_PASSWORD_ERROR_OVER_LIMIT_T, new Object[]{errorNum});
        }
      }
    }
  }

  public void recordSignInPasswordErrorNum(Long tenantId, String innerAccount) {
    String passwordLockedCacheKey = String.format(CACHE_PASSWORD_ERROR_LOCKED_PREFIX, innerAccount);
    String passwordLockedMinutes = stringRedisService.get(passwordLockedCacheKey);
    if (Objects.nonNull(passwordLockedMinutes)) {
      // Do not record the number of errors after locking
      return;
    }
    String passwordErrorNumCacheKey = String.format(CACHE_PASSWORD_ERROR_NUM_PREFIX, innerAccount);
    String passwordErrorNum = stringRedisService.get(passwordErrorNumCacheKey);
    if (Objects.nonNull(passwordErrorNum)) {
      passwordErrorNum = String.valueOf(parseInt(passwordErrorNum) + 1);
    } else {
      passwordErrorNum = String.valueOf(1);
    }
    stringRedisService.set(passwordErrorNumCacheKey, passwordErrorNum);
    SettingTenant settingTenant = authUserSignQuery.checkAndFindSettingTenant(tenantId);
    // When sign-in limit is enabled
    SigninLimit signinLimit = settingTenant.getSecurityData().getSigninLimit();
    if (signinLimit.getEnabled()
        && Integer.parseInt(passwordErrorNum) >= signinLimit.getLockedPassdErrorNum()) {
      stringRedisService.set(passwordLockedCacheKey, passwordErrorNum,
          signinLimit.getPassdErrorIntervalInMinutes(), TimeUnit.MINUTES);
      stringRedisService.delete(passwordErrorNumCacheKey);
    }
  }

  @Override
  protected BaseRepository<AuthUser, Long> getRepository() {
    return this.authUserRepo;
  }
}
