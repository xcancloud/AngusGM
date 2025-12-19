package cloud.xcan.angus.core.gm.application.query.auth.impl;

import static cloud.xcan.angus.api.commonlink.UCConstant.CACHE_EMAIL_CHECK_SECRET_PREFIX;
import static cloud.xcan.angus.api.commonlink.UCConstant.CACHE_SMS_CHECK_SECRET_PREFIX;
import static cloud.xcan.angus.api.commonlink.client.ClientSource.isOperationClientSignIn;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertNotEmpty;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceNotFound;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.gm.domain.AuthMessage.SIGN_IN_PASSWORD_ERROR;
import static cloud.xcan.angus.core.gm.domain.UserMessage.LINK_SECRET_ILLEGAL_ERROR;
import static cloud.xcan.angus.core.gm.domain.UserMessage.LINK_SECRET_TIMEOUT_ERROR;
import static cloud.xcan.angus.remote.message.ProtocolException.M.ACCOUNT_PASSWORD_ERROR;
import static cloud.xcan.angus.remote.message.ProtocolException.M.ACCOUNT_PASSWORD_ERROR_KEY;
import static cloud.xcan.angus.remote.message.ProtocolException.M.USER_DISABLED_KEY;
import static cloud.xcan.angus.remote.message.ProtocolException.M.USER_DISABLED_T;
import static cloud.xcan.angus.remote.message.ProtocolException.M.USER_EXPIRED_KEY;
import static cloud.xcan.angus.remote.message.ProtocolException.M.USER_EXPIRED_T;
import static cloud.xcan.angus.remote.message.ProtocolException.M.USER_LOCKED_KEY;
import static cloud.xcan.angus.remote.message.ProtocolException.M.USER_LOCKED_T;
import static cloud.xcan.angus.spec.experimental.BizConstant.OWNER_TENANT_ID;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.authuser.AuthUser;
import cloud.xcan.angus.api.commonlink.authuser.AuthUserRepo;
import cloud.xcan.angus.api.commonlink.email.EmailBizKey;
import cloud.xcan.angus.api.commonlink.sms.SmsBizKey;
import cloud.xcan.angus.api.enums.SignInType;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.auth.AuthUserQuery;
import cloud.xcan.angus.lettucex.util.RedisService;
import cloud.xcan.angus.remote.message.ProtocolException;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import cloud.xcan.angus.security.model.CustomOAuth2User;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * <p>
 * Implementation of authentication user query operations.
 * </p>
 * <p>
 * Manages authentication user retrieval, validation, and verification. Provides comprehensive user
 * authentication querying with security checks.
 * </p>
 * <p>
 * Supports user authentication, password validation, link secret verification, and user status
 * validation for secure authentication management.
 * </p>
 */
@org.springframework.stereotype.Service
public class AuthUserQueryImpl implements AuthUserQuery {

  @Resource
  private AuthUserRepo authUserRepo;
  @Resource
  private PasswordEncoder passwordEncoder;
  @Resource
  private RedisService<String> stringRedisService;

  /**
   * <p>
   * Authenticates users by account and password.
   * </p>
   * <p>
   * Verifies account exists and validates password using password encoder. Returns filtered list of
   * users with matching credentials.
   * </p>
   */
  @Override
  public List<AuthUser> findByAccountAndPassword(String account, String password) {
    return new BizTemplate<List<AuthUser>>(false) {
      List<AuthUser> usersDb = null;

      @Override
      protected void checkParams() {
        // Verify account exists
        usersDb = authUserRepo.findByAccount(account);
      }

      @Override
      protected List<AuthUser> process() {
        if (isEmpty(usersDb)) {
          return null;
        }
        // Fix: The value is empty when no password is set
        usersDb = usersDb.stream().filter(user -> isNotEmpty(user.getPassword())
            && passwordEncoder.matches(password, user.getPassword())).collect(Collectors.toList());
        // NOOP: Limit login failures number by sign-in
        // BizAssert.assertTrue(CollectionUtils.isNotEmpty(usersDb), ACCOUNT_PASSWORD_ERROR);
        return usersDb;
      }
    }.execute();
  }

  /**
   * <p>
   * Validates user password by user ID.
   * </p>
   * <p>
   * Verifies user exists and validates provided password against stored password. Throws
   * appropriate exception if password validation fails.
   * </p>
   */
  @Override
  public void checkPassword(Long id, String password) {
    new BizTemplate<Void>() {
      AuthUser userDb = null;

      @Override
      protected void checkParams() {
        userDb = checkAndFind(id);
      }

      @Override
      protected Void process() {
        assertTrue(passwordEncoder.matches(password, userDb.getPassword()), SIGN_IN_PASSWORD_ERROR);
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Validates and retrieves user by account with authentication.
   * </p>
   * <p>
   * Handles multi-tenant scenarios and different sign-in types. Supports user ID-based login
   * control and password-based user selection.
   * </p>
   */
  @Override
  public AuthUser checkAndFindByAccount(Long userId, SignInType signinType, String account,
      String password) {
    AuthUser finalUser = null;
    if (nonNull(userId)) {
      // When the account has multiple tenants or SMS login, control the login using the username by userId
      // Multi-account login should set the userId
      finalUser = authUserRepo.findById(userId).orElse(null);
    } else if (SignInType.ACCOUNT_PASSWORD.equals(signinType)) {
      // When the userId is not specified, the first username that matches the password will be used by default to login
      List<AuthUser> users = authUserRepo.findByAccount(account);
      if (isNotEmpty(users)) {
        if (users.size() == 1) {
          return users.get(0);
        }
        // Find according to password matching when multiple users
        for (AuthUser user : users) {
          // Fix: There is no PasswordEncoder mapped for the id "null" when password not set <- isNotEmpty(user.getPassword())
          if (isNotEmpty(user.getPassword())
              && passwordEncoder.matches(password, user.getPassword())) {
            finalUser = users.get(0);
            break;
          }
        }
      }
    }
    assertTrue(nonNull(finalUser), ACCOUNT_PASSWORD_ERROR, ACCOUNT_PASSWORD_ERROR_KEY);
    return finalUser;
  }

  /**
   * <p>
   * Validates email link secret for user operations.
   * </p>
   * <p>
   * Verifies link secret from Redis cache for email modification operations. Supports different
   * business keys for various email operations.
   * </p>
   */
  @Override
  public void checkLinkSecret(Long userId, String linkSecret, EmailBizKey bizKey) {
    if (bizKey.sameValueAs(EmailBizKey.MODIFY_EMAIL)) {
      String emailCacheKey = String.format(CACHE_EMAIL_CHECK_SECRET_PREFIX, bizKey, userId);
      String emailLinkSecret = stringRedisService.get(emailCacheKey);
      assertNotEmpty(emailLinkSecret, LINK_SECRET_TIMEOUT_ERROR);
      assertTrue(StringUtils.equals(emailLinkSecret, linkSecret), LINK_SECRET_ILLEGAL_ERROR,
          new String[]{linkSecret});
      stringRedisService.delete(emailCacheKey);
    } else if (bizKey.sameValueAs(EmailBizKey.BIND_EMAIL)) {
      checkPassword(userId, linkSecret);
    } else {
      throw ProtocolException.of(String.format("bizKey %s error", bizKey.getValue()));
    }
  }

  /**
   * <p>
   * Validates SMS link secret for user operations.
   * </p>
   * <p>
   * Verifies link secret from Redis cache for mobile modification operations. Supports different
   * business keys for various SMS operations.
   * </p>
   */
  @Override
  public void checkSmsLinkSecret(Long userId, String linkSecret, SmsBizKey bizKey) {
    if (bizKey.equals(SmsBizKey.MODIFY_MOBILE)) {
      String smsCacheKey = String.format(CACHE_SMS_CHECK_SECRET_PREFIX, bizKey, userId);
      String smsLinkSecret = stringRedisService.get(smsCacheKey);
      assertNotEmpty(smsLinkSecret, LINK_SECRET_TIMEOUT_ERROR);
      assertTrue(StringUtils.equals(smsLinkSecret, linkSecret), LINK_SECRET_ILLEGAL_ERROR,
          new String[]{linkSecret});
      stringRedisService.delete(smsCacheKey);
    } else if (bizKey.equals(SmsBizKey.BIND_MOBILE)) {
      checkPassword(userId, linkSecret);
    } else {
      throw ProtocolException.of(String.format("bizKey %s error", bizKey.getValue()));
    }
  }

  /**
   * <p>
   * Validates user account status and validity.
   * </p>
   * <p>
   * Checks if user is enabled, not locked, and not expired. Throws appropriate exceptions for
   * invalid user status.
   * </p>
   */
  @Override
  public void checkUserValid(AuthUser user) {
    assertTrue(user.isEnabled(), USER_DISABLED_T, USER_DISABLED_KEY,
        new Object[]{user.getFullName()});

    assertTrue(user.isAccountNonLocked(), USER_LOCKED_T, USER_LOCKED_KEY,
        new Object[]{user.getFullName()});

    assertTrue(user.isAccountNonExpired(), USER_EXPIRED_T, USER_EXPIRED_KEY,
        new Object[]{user.getFullName()});
  }

  /**
   * <p>
   * Validates operation platform login access.
   * </p>
   * <p>
   * Ensures only authorized users can access operation platform. Validates tenant ID and user role
   * requirements.
   * </p>
   */
  @Override
  public void checkOperationPlatformLogin(CustomOAuth2User user) {
    if (nonNull(user.getClientSource()) && isOperationClientSignIn(user.getClientSource())) {
      if (!OWNER_TENANT_ID.toString().equals(user.getTenantId())
          || !(user.isSysAdmin() || user.isToUser())) {
        throw new InsufficientAuthenticationException(
            "Illegal access, prohibited from logging into the operation platform");
      }
    }
  }

  /**
   * <p>
   * Validates and retrieves multiple authentication users by IDs.
   * </p>
   * <p>
   * Verifies all users exist and validates complete collection match. Throws appropriate exceptions
   * for missing or invalid users.
   * </p>
   */
  @Override
  public List<AuthUser> checkAndFind(List<Long> ids) {
    if (isEmpty(ids)) {
      return null;
    }
    List<AuthUser> users = authUserRepo.findAllByIdIn(
        ids.stream().map(Object::toString).collect(Collectors.toSet()));
    assertResourceNotFound(isNotEmpty(users), ids.iterator().next(), "AuthUser");
    if (ids.size() != users.size()) {
      for (AuthUser user : users) {
        assertResourceNotFound(ids.contains(Long.valueOf(user.getId())), user.getId(), "AuthUser");
      }
    }
    return users;
  }

  /**
   * <p>
   * Validates and retrieves authentication user by ID.
   * </p>
   * <p>
   * Verifies user exists and returns user information. Throws ResourceNotFound exception if user
   * does not exist.
   * </p>
   */
  @Override
  public AuthUser checkAndFind(Long id) {
    return authUserRepo.findById(id).orElseThrow(() -> ResourceNotFound.of(id, "AuthUser"));
  }

  /**
   * <p>
   * Retrieves authentication user by username.
   * </p>
   * <p>
   * Returns user information for the specified username.
   * </p>
   */
  @Override
  public AuthUser findByUsername(String username) {
    return authUserRepo.findByUsername(username);
  }

  /**
   * <p>
   * Retrieves authentication users by email address.
   * </p>
   * <p>
   * Returns list of users associated with the specified email address.
   * </p>
   */
  @Override
  public List<AuthUser> findByEmail(String email) {
    return authUserRepo.findByEmail(email);
  }

  /**
   * <p>
   * Retrieves authentication users by mobile number.
   * </p>
   * <p>
   * Returns list of users associated with the specified mobile number.
   * </p>
   */
  @Override
  public List<AuthUser> findByMobile(String mobile) {
    return authUserRepo.findByMobile(mobile);
  }
}
