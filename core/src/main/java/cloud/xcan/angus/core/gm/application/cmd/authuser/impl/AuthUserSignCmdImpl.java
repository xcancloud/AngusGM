package cloud.xcan.angus.core.gm.application.cmd.authuser.impl;

import static cloud.xcan.angus.api.commonlink.AASConstant.CACHE_PASSWORD_ERROR_LOCKED_PREFIX;
import static cloud.xcan.angus.api.commonlink.AASConstant.CACHE_PASSWORD_ERROR_NUM_PREFIX;
import static cloud.xcan.angus.api.commonlink.UCConstant.PASSWORD_ENCRYP_TYPE_SUFFIX;
import static cloud.xcan.angus.api.commonlink.UCConstant.PASSWORD_PROXY_ENCRYP;
import static cloud.xcan.angus.api.commonlink.UCConstant.PASSWORD_PROXY_ENCRYP_TYPE;
import static cloud.xcan.angus.api.commonlink.client.ClientSource.isUserSignIn;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
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
import static cloud.xcan.angus.remote.message.ProtocolException.M.CLIENT_NOT_FOUND;
import static cloud.xcan.angus.remote.message.ProtocolException.M.CLIENT_NOT_FOUND_KEY;
import static cloud.xcan.angus.remote.message.ProtocolException.M.PARAM_MISSING_KEY;
import static cloud.xcan.angus.remote.message.ProtocolException.M.QUERY_FIELD_EMPTY_T;
import static cloud.xcan.angus.security.authentication.password.OAuth2PasswordAuthenticationProviderUtils.DEFAULT_ENCODING_ID;
import static cloud.xcan.angus.spec.http.MediaType.APPLICATION_FORM_URLENCODED;
import static java.lang.Integer.parseInt;
import static java.lang.String.format;
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
import cloud.xcan.angus.core.spring.SpringContextHolder;
import cloud.xcan.angus.core.spring.boot.ApplicationInfo;
import cloud.xcan.angus.core.utils.ValidatorUtils;
import cloud.xcan.angus.lettucex.util.RedisService;
import cloud.xcan.angus.remote.message.ProtocolException;
import cloud.xcan.angus.remote.message.SysException;
import cloud.xcan.angus.remote.message.http.Unauthorized;
import cloud.xcan.angus.security.authentication.dao.DaoAuthenticationProvider;
import cloud.xcan.angus.security.client.CustomOAuth2RegisteredClient;
import cloud.xcan.angus.security.model.CustomOAuth2User;
import cloud.xcan.angus.spec.annotations.DoInFuture;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
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
  public Map<String, String> signin(String clientId, String clientSecret, SignInType signinType,
      @Nullable Long userId, String account, String password, String scope, String deviceId) {
    return new BizTemplate<Map<String, String>>(false) {
      CustomOAuth2RegisteredClient clientDb;
      AuthUser authUserDb;

      @Override
      protected void checkParams() {
        // Check the required account parameters
        checkRequiredParameters(userId, account, deviceId);
        // Check the clientId, clientSecret and scopes are correct
        clientDb = clientQuery.checkAndFind(clientId, clientSecret, scope);
        // Check and find the existed account
        authUserDb = authUserQuery.checkAndFindByAccount(userId, signinType, account, password);
        // Check the number of password errors
        checkSignInPasswordErrorNum(authUserDb.getTenantId(), authUserDb.getUsername());
      }

      @Override
      protected Map<String, String> process() {
        try {
          // Cached to context for LdapPasswordConnection login
          cacheUserDirectory(authUserDb);

          // Cached to context for load UserDetail
          daoAuthenticationProvider.getUserCache().putUserInCache(authUserDb.getId(),
              authUserDb.getUsername(), AuthUser.with(authUserDb));

          // Submit OAuth2 login authentication
          Map<String, String> result = submitOauth2SignInRequest(clientId, clientSecret,
              signinType, authUserDb.getId(), account, password, scope);

          // Save new bcrypt password after login directory success
          updateNewDirectoryPassword(authUserDb, password);
          return result;
        } catch (Throwable e) {
          if (nonNull(authUserDb)) {
            recordSignInPasswordErrorNum(Long.valueOf(authUserDb.getTenantId()),
                authUserDb.getUsername());
          }
          if (e instanceof Unauthorized) {
            throw (Unauthorized) e;
          }
          throw new SysException(e.getMessage());
        } finally {
          // Record successful login logs
          if (nonNull(operationEventQueue) && nonNull(authUserDb)) {
            operationEventQueue.add(getSignupOperationEvent(clientId, authUserDb));
          }
        }
      }
    }.execute();
  }

  @Override
  public Map<String, String> renew(String clientId, String clientSecret, String refreshToken) {
    return new BizTemplate<Map<String, String>>(false) {
      CustomOAuth2RegisteredClient clientDb;

      @Override
      protected void checkParams() {
        // Check the clientId, clientSecret and scopes are correct
        clientDb = clientQuery.checkAndFind(clientId, clientSecret);
      }

      @Override
      protected Map<String, String> process() {
        // Submit OAuth2 refresh token authentication
        try {
          return submitOauth2RenewRequest(clientId, clientSecret, refreshToken);
        } catch (Throwable e) {
          throw new SysException(e.getMessage());
        }
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
          assertTrue(clientDb.getId().equals(authorizationDb.getRegisteredClientId()),
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
        authUserSignQuery.checkMinPasswordLengthByTenantSetting(
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
      UserDirectory userDirectory = userDirectoryRepo.findById(Long.valueOf(user.getDirectoryId()))
          .orElse(null);
      if (nonNull(userDirectory) && nonNull(userDirectory.getEnabled())
          && userDirectory.getEnabled()) {
        PrincipalContext.addExtension("userDirectory", userDirectory);
        PrincipalContext.addExtension("fullName", user.getFullName()); // LDAP -> CN
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

  private Map<String, String> submitOauth2SignInRequest(String clientId, String clientSecret,
      SignInType signinType, String userId, String account, String password, String scope)
      throws Throwable {
    String authContent = format(
        "client_id=%s&client_secret=%s&grant_type=%s&user_id=%s&account=%s&password=%s",
        clientId, clientSecret, signinType.toOAuth2GrantType(), userId, account, password);
    if (isNotEmpty(scope)) {
      authContent = authContent + "&scope=" + scope;
    }
    return sendOauth2RenewRequest(authContent);
  }

  private Map<String, String> submitOauth2RenewRequest(String clientId, String clientSecret,
      String refreshToken) throws Throwable {
    String authContent = format("client_id=%s&client_secret=%s&grant_type=%s&refresh_token=%s",
        clientId, clientSecret, AuthorizationGrantType.REFRESH_TOKEN.getValue(), refreshToken);
    return sendOauth2RenewRequest(authContent);
  }

  public static Map<String, String> sendOauth2RenewRequest(String authContent) throws Throwable {
    HttpSender sender = new HttpUrlConnectionSender();
    ApplicationInfo applicationInfo = SpringContextHolder.getBean(ApplicationInfo.class);
    String tokenEndpoint = format("http://%s/oauth2/token", applicationInfo.getInstanceId());
    Response response = Request.build(tokenEndpoint, sender).withMethod(HttpMethod.POST)
        .withContent(APPLICATION_FORM_URLENCODED, authContent).send();
    Map<String, String> result = JsonUtils.convert(response.body(), new TypeReference<>() {
    });
    if (!response.isSuccessful()) {
      if (response.code() == HttpStatus.UNAUTHORIZED.value) {
        throw Unauthorized.of(Objects.requireNonNull(result).get("error_description"));
      } else {
        throw ProtocolException.of(Objects.requireNonNull(result).get("error_description"));
      }
    }
    return result;
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
    String emailCacheKey = format(AASConstant.CACHE_EMAIL_CHECK_SECRET_PREFIX,
        EmailBizKey.PASSD_FORGET, userId);
    String smsCacheKey = format(AASConstant.CACHE_SMS_CHECK_SECRET_PREFIX,
        SmsBizKey.PASSD_FORGET, userId);
    String emailLinkSecret = stringRedisService.get(emailCacheKey);
    String smsLinkSecret = stringRedisService.get(smsCacheKey);
    assertTrue(isNotBlank(emailLinkSecret) || isNotBlank(smsLinkSecret), LINK_SECRET_TIMEOUT);
    assertTrue(StringUtils.equals(smsLinkSecret, linkSecret)
        || StringUtils.equalsIgnoreCase(emailLinkSecret, linkSecret), LINK_SECRET_ILLEGAL);
    stringRedisService.delete(emailCacheKey);
    stringRedisService.delete(smsCacheKey);
  }

  private void checkSignInPasswordErrorNum(String tenantId, String finalAccount) {
    String passwordLockedCacheKey = format(CACHE_PASSWORD_ERROR_LOCKED_PREFIX, finalAccount);
    String passwordLockedMinutes = stringRedisService.get(passwordLockedCacheKey);
    BizAssert.assertTrue(isNull(passwordLockedMinutes), SIGN_IN_PASSWORD_ERROR_LOCKED_RETRY_CODE,
        SIGN_IN_PASSWORD_ERROR_LOCKED_RETRY_T, new Object[]{passwordLockedMinutes});

    String passwordErrorNumCacheKey = format(CACHE_PASSWORD_ERROR_NUM_PREFIX, finalAccount);
    String passwordErrorNum = stringRedisService.get(passwordErrorNumCacheKey);
    if (nonNull(passwordErrorNum)) {
      SettingTenant settingTenant = authUserSignQuery.checkAndFindSettingTenant(
          Long.valueOf(tenantId));
      SigninLimit signinLimit = settingTenant.getSecurityData().getSigninLimit();
      // When sign-in limit is enabled
      if (signinLimit.getEnabled()) {
        Integer errorNum = signinLimit.getLockedPasswordErrorNum();
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
    String passwordLockedCacheKey = format(CACHE_PASSWORD_ERROR_LOCKED_PREFIX, innerAccount);
    String passwordLockedMinutes = stringRedisService.get(passwordLockedCacheKey);
    if (Objects.nonNull(passwordLockedMinutes)) {
      // Do not record the number of errors after locking
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
    SettingTenant settingTenant = authUserSignQuery.checkAndFindSettingTenant(tenantId);
    // When sign-in limit is enabled
    SigninLimit signinLimit = settingTenant.getSecurityData().getSigninLimit();
    if (signinLimit.getEnabled()
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
