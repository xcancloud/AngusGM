package cloud.xcan.angus.core.gm.application.query.auth.impl;

import static cloud.xcan.angus.api.commonlink.AuthConstant.CACHE_EMAIL_CHECK_SECRET_PREFIX;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceNotFound;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.gm.domain.AuthMessage.PASSWORD_IS_TOO_SHORT_T;
import static cloud.xcan.angus.remote.message.ProtocolException.M.EMAIL_NOT_EXIST_T;
import static cloud.xcan.angus.remote.message.ProtocolException.M.MOBILE_NOT_EXIST_T;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_LINK_SECRET_LENGTH;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

import cloud.xcan.angus.api.commonlink.AuthConstant;
import cloud.xcan.angus.api.commonlink.authuser.AuthUser;
import cloud.xcan.angus.api.commonlink.email.EmailBizKey;
import cloud.xcan.angus.api.commonlink.setting.tenant.SettingTenant;
import cloud.xcan.angus.api.commonlink.setting.tenant.SettingTenantRepo;
import cloud.xcan.angus.api.commonlink.sms.SmsBizKey;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.cmd.email.EmailCmd;
import cloud.xcan.angus.core.gm.application.cmd.sms.SmsCmd;
import cloud.xcan.angus.core.gm.application.query.auth.AuthUserQuery;
import cloud.xcan.angus.core.gm.application.query.auth.AuthUserSignQuery;
import cloud.xcan.angus.lettucex.util.RedisService;
import cloud.xcan.angus.remote.message.SysException;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Implementation of authentication user sign-in query operations.
 * </p>
 * <p>
 * Manages user sign-in verification, email/SMS validation, and password policy checks. Provides
 * comprehensive sign-in querying with security validation support.
 * </p>
 * <p>
 * Supports email verification, SMS verification, password policy validation, and tenant setting
 * retrieval for secure user sign-in management.
 * </p>
 */
@org.springframework.stereotype.Service
public class AuthUserSignQueryImpl implements AuthUserSignQuery {

  @Resource
  private AuthUserQuery authUserQuery;
  @Resource
  private SettingTenantRepo settingTenantRepo;
  @Resource
  private SmsCmd smsCmd;
  @Resource
  private EmailCmd emailCmd;
  @Resource
  private RedisService<String> stringRedisService;

  /**
   * <p>
   * Validates email verification and retrieves associated users.
   * </p>
   * <p>
   * Verifies email verification code and generates link secrets for users. Stores link secrets in
   * Redis cache for secure email operations.
   * </p>
   */
  @Override
  public List<AuthUser> checkEmail(String email, EmailBizKey bizKey, String verificationCode) {
    return new BizTemplate<List<AuthUser>>() {
      List<AuthUser> users;

      @Override
      protected void checkParams() {
        // Verify email verification code is correct
        emailCmd.checkVerificationCode(bizKey, email, verificationCode);
        // Verify email user exists
        users = checkEmailUserExist(email);
      }

      @Override
      protected List<AuthUser> process() {
        users.forEach(user -> {
          String linkSecret = randomAlphabetic(MAX_LINK_SECRET_LENGTH);
          user.setLinkSecret(linkSecret);
          stringRedisService.set(String.format(CACHE_EMAIL_CHECK_SECRET_PREFIX, bizKey,
              user.getId()), linkSecret, AuthConstant.LINK_SECRET_VALID_SECOND, TimeUnit.SECONDS);
        });
        return users;
      }
    }.execute();
  }

  /**
   * <p>
   * Validates SMS verification and retrieves associated users.
   * </p>
   * <p>
   * Verifies SMS verification code and generates link secrets for users. Stores link secrets in
   * Redis cache for secure mobile operations.
   * </p>
   */
  @Override
  public List<AuthUser> checkSms(String mobile, SmsBizKey bizKey, String verificationCode) {
    return new BizTemplate<List<AuthUser>>() {
      List<AuthUser> users;

      @Override
      protected void checkParams() {
        // Verify SMS verification code is correct
        smsCmd.checkVerificationCode(bizKey, mobile, verificationCode);
        // Verify mobile user exists
        users = checkMobileUserExist(mobile);
      }

      @Override
      protected List<AuthUser> process() {
        users.forEach(u -> {
          String linkSecret = randomAlphabetic(MAX_LINK_SECRET_LENGTH);
          u.setLinkSecret(linkSecret);
          stringRedisService.set(
              String.format(AuthConstant.CACHE_SMS_CHECK_SECRET_PREFIX, bizKey, u.getId()),
              linkSecret, AuthConstant.LINK_SECRET_VALID_SECOND, TimeUnit.SECONDS);
        });
        return users;
      }
    }.execute();
  }

  /**
   * <p>
   * Validates password length against tenant security policy.
   * </p>
   * <p>
   * Checks if password meets minimum length requirement defined in tenant settings. Throws
   * appropriate exception if password is too short.
   * </p>
   */
  @Override
  public void checkMinPasswordLengthByTenantSetting(Long tenantId, String password) {
    SettingTenant settingTenant = checkAndFindSettingTenant(tenantId);
    assertTrue(Objects.isNull(settingTenant.getSecurityData())
            || password.length() >= settingTenant.getSecurityData().getPasswordPolicy()
            .getMinLength(), PASSWORD_IS_TOO_SHORT_T,
        new Object[]{settingTenant.getSecurityData().getPasswordPolicy().getMinLength()});
  }

  /**
   * <p>
   * Validates and retrieves tenant settings.
   * </p>
   * <p>
   * Verifies tenant settings exist and returns setting information. Throws SysException if tenant
   * settings are not initialized.
   * </p>
   */
  @Override
  public SettingTenant checkAndFindSettingTenant(Long tenantId) {
    return settingTenantRepo.findByTenantId(tenantId).orElseThrow(() ->
        SysException.of(String.format("Tenant %s setting is not initialized", tenantId)));
  }

  /**
   * <p>
   * Validates email user existence.
   * </p>
   * <p>
   * Verifies that users exist for the specified email address. Throws appropriate exception if no
   * users found.
   * </p>
   */
  private List<AuthUser> checkEmailUserExist(String email) {
    List<AuthUser> users = authUserQuery.findByEmail(email);
    assertResourceNotFound(users, EMAIL_NOT_EXIST_T, new Object[]{email});
    return users;
  }

  /**
   * <p>
   * Validates mobile user existence.
   * </p>
   * <p>
   * Verifies that users exist for the specified mobile number. Throws appropriate exception if no
   * users found.
   * </p>
   */
  private List<AuthUser> checkMobileUserExist(String mobile) {
    List<AuthUser> users = authUserQuery.findByMobile(mobile);
    assertResourceNotFound(users, MOBILE_NOT_EXIST_T, new Object[]{mobile});
    return users;
  }
}
