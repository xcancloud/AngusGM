package cloud.xcan.angus.core.gm.application.query.authuser.impl;


import static cloud.xcan.angus.api.commonlink.AASConstant.CACHE_EMAIL_CHECK_SECRET_PREFIX;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceNotFound;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.gm.domain.AASCoreMessage.PASSWORD_IS_TOO_SHORT_T;
import static cloud.xcan.angus.remote.message.CommProtocolException.M.EMAIL_NOT_EXIST_T;
import static cloud.xcan.angus.remote.message.CommProtocolException.M.MOBILE_NOT_EXIST_T;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_LINK_SECRET_LENGTH;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

import cloud.xcan.angus.api.commonlink.AASConstant;
import cloud.xcan.angus.api.commonlink.authuser.AuthUser;
import cloud.xcan.angus.api.commonlink.email.EmailBizKey;
import cloud.xcan.angus.api.commonlink.setting.tenant.SettingTenant;
import cloud.xcan.angus.api.commonlink.setting.tenant.SettingTenantRepo;
import cloud.xcan.angus.api.commonlink.sms.SmsBizKey;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.cmd.email.EmailCmd;
import cloud.xcan.angus.core.gm.application.cmd.sms.SmsCmd;
import cloud.xcan.angus.core.gm.application.query.authuser.AuthUserQuery;
import cloud.xcan.angus.core.gm.application.query.authuser.AuthUserSignQuery;
import cloud.xcan.angus.lettucex.util.RedisService;
import cloud.xcan.angus.remote.message.CommSysException;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


@Biz
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

  @Override
  public List<AuthUser> checkEmail(String email, EmailBizKey bizKey, String verificationCode) {
    return new BizTemplate<List<AuthUser>>() {
      List<AuthUser> users;

      @Override
      protected void checkParams() {
        // Check whether the verification code is correct
        emailCmd.checkVerificationCode(bizKey, email, verificationCode);
        // Check email user is existed
        users = checkEmailUserExist(email);
      }

      @Override
      protected List<AuthUser> process() {
        users.forEach(user -> {
          String linkSecret = randomAlphabetic(MAX_LINK_SECRET_LENGTH);
          user.setLinkSecret(linkSecret);
          stringRedisService.set(String.format(CACHE_EMAIL_CHECK_SECRET_PREFIX, bizKey,
              user.getId()), linkSecret, AASConstant.LINK_SECRET_VALID_SECOND, TimeUnit.SECONDS);
        });
        return users;
      }
    }.execute();
  }

  @Override
  public List<AuthUser> checkSms(String mobile, SmsBizKey bizKey, String verificationCode) {
    return new BizTemplate<List<AuthUser>>() {
      List<AuthUser> users;

      @Override
      protected void checkParams() {
        // Check whether the verification code is correct
        smsCmd.checkVerificationCode(bizKey, mobile, verificationCode);
        // Check mobile user is existed
        users = checkMobileUserExist(mobile);
      }

      @Override
      protected List<AuthUser> process() {
        users.forEach(u -> {
          String linkSecret = randomAlphabetic(MAX_LINK_SECRET_LENGTH);
          u.setLinkSecret(linkSecret);
          stringRedisService.set(
              String.format(AASConstant.CACHE_SMS_CHECK_SECRET_PREFIX, bizKey, u.getId()),
              linkSecret, AASConstant.LINK_SECRET_VALID_SECOND, TimeUnit.SECONDS);
        });
        return users;
      }
    }.execute();
  }

  @Override
  public void checkMinPasswordLengthByTenantSetting(Long tenantId, String password) {
    SettingTenant settingTenant = checkAndFindSettingTenant(tenantId);
    assertTrue(Objects.isNull(settingTenant.getSecurityData())
            || password.length() >= settingTenant.getSecurityData().getPasswordPolicy()
            .getMinLength(), PASSWORD_IS_TOO_SHORT_T,
        new Object[]{settingTenant.getSecurityData().getPasswordPolicy().getMinLength()});
  }

  @Override
  public SettingTenant checkAndFindSettingTenant(Long tenantId) {
    return settingTenantRepo.findByTenantId(tenantId).orElseThrow(() ->
        CommSysException.of(String.format("Tenant %s setting is not initialized", tenantId)));
  }

  private List<AuthUser> checkEmailUserExist(String email) {
    List<AuthUser> users = authUserQuery.findByEmail(email);
    assertResourceNotFound(users, EMAIL_NOT_EXIST_T, new Object[]{email});
    return users;
  }

  private List<AuthUser> checkMobileUserExist(String mobile) {
    List<AuthUser> users = authUserQuery.findByMobile(mobile);
    assertResourceNotFound(users, MOBILE_NOT_EXIST_T, new Object[]{mobile});
    return users;
  }
}
