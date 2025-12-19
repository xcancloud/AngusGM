package cloud.xcan.angus.core.gm.application.query.user.impl;

import static cloud.xcan.angus.api.commonlink.UCConstant.CACHE_EMAIL_CHECK_SECRET_PREFIX;
import static cloud.xcan.angus.api.commonlink.UCConstant.CACHE_SMS_CHECK_SECRET_PREFIX;
import static cloud.xcan.angus.api.commonlink.UCConstant.DEFAULT_SECRET_VALID_SECOND;
import static cloud.xcan.angus.api.commonlink.email.EmailBizKey.BIND_EMAIL;
import static cloud.xcan.angus.api.commonlink.sms.SmsBizKey.BIND_MOBILE;
import static cloud.xcan.angus.core.gm.domain.UserMessage.EMAIL_BIND_EXISTED;
import static cloud.xcan.angus.core.gm.domain.UserMessage.MOBILE_BIND_EXISTED;
import static cloud.xcan.angus.core.gm.domain.UserMessage.SEND_EMAIL_ERROR_T;
import static cloud.xcan.angus.core.gm.domain.UserMessage.SEND_MOBILE_ERROR_T;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_LINK_SECRET_LENGTH;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

import cloud.xcan.angus.api.commonlink.email.EmailBizKey;
import cloud.xcan.angus.api.commonlink.sms.SmsBizKey;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.commonlink.user.UserRepo;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.ProtocolAssert;
import cloud.xcan.angus.core.gm.application.cmd.email.EmailCmd;
import cloud.xcan.angus.core.gm.application.cmd.sms.SmsCmd;
import cloud.xcan.angus.core.gm.application.query.user.UserCurrentQuery;
import cloud.xcan.angus.core.gm.application.query.user.UserQuery;
import cloud.xcan.angus.core.gm.domain.email.Email;
import cloud.xcan.angus.core.gm.domain.sms.Sms;
import cloud.xcan.angus.core.utils.ValidatorUtils;
import cloud.xcan.angus.lettucex.util.RedisService;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Implementation of current user query operations.
 * </p>
 * <p>
 * Manages current user information retrieval, SMS/email verification, and binding. Provides
 * comprehensive current user querying with verification support.
 * </p>
 * <p>
 * Supports current user detail retrieval, SMS/email sending, verification code checking, and
 * binding operations for comprehensive current user administration.
 * </p>
 */
@org.springframework.stereotype.Service
public class UserCurrentQueryImpl implements UserCurrentQuery {

  @Resource
  private UserRepo userRepo;
  @Resource
  private UserQuery userQuery;
  @Resource
  private SmsCmd smsCmd;
  @Resource
  private EmailCmd emailCmd;
  @Resource
  private RedisService<String> stringRedisService;

  /**
   * <p>
   * Retrieves detailed current user information.
   * </p>
   * <p>
   * Fetches current user details with optional association joining. Uses current user context for
   * data retrieval.
   * </p>
   */
  @Override
  public User currentDetail(boolean joinAssoc) {
    return new BizTemplate<User>() {

      @Override
      protected User process() {
        return userQuery.detail(getUserId(), joinAssoc);
      }
    }.execute();
  }

  /**
   * <p>
   * Sends SMS verification to current user.
   * </p>
   * <p>
   * Validates mobile format and user ownership before sending SMS. Ensures mobile belongs to
   * current user for security.
   * </p>
   */
  @Override
  public void sendSms(Sms sms, String country) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        String mobile = sms.getInputParamData().getMobiles().iterator().next();

        // Check mobile format
        ValidatorUtils.checkMobile(country, mobile);

        // Noop: Use @EnumConstant instead of check that the bizkey is correct

        // Check whether the mobile is the current person's own mobile
        checkUserMobile(mobile, sms.getBizKey());
      }

      @Override
      protected Void process() {
        smsCmd.send(sms, false);
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Validates SMS verification code and generates link secret.
   * </p>
   * <p>
   * Checks verification code correctness and user ownership. Generates and stores link secret for
   * binding confirmation.
   * </p>
   */
  @Override
  public String checkSms(SmsBizKey bizKey, String mobile, String country, String verificationCode) {
    return new BizTemplate<String>() {
      final Long userId = getUserId();

      @Override
      protected void checkParams() {
        // Check mobile format
        ValidatorUtils.checkMobile(country, mobile);

        // Noop: Use @EnumConstant instead of check that the bizkey is correct

        // Check whether the mobile is the current person's own mobile
        checkUserMobile(mobile, bizKey);

        // Check the mobile verification code is correct
        smsCmd.checkVerificationCode(bizKey, mobile, verificationCode);

        // Check the modified mobile exists
        userQuery.checkUpdateMobile(mobile, userId);
      }

      @Override
      protected String process() {
        String linkSecret = randomAlphabetic(MAX_LINK_SECRET_LENGTH);
        stringRedisService.set(String.format(CACHE_SMS_CHECK_SECRET_PREFIX, bizKey, userId),
            linkSecret, DEFAULT_SECRET_VALID_SECOND, TimeUnit.SECONDS);
        return linkSecret;
      }
    }.execute();
  }

  /**
   * <p>
   * Sends email verification to current user.
   * </p>
   * <p>
   * Validates email ownership before sending email. Ensures email belongs to current user for
   * security.
   * </p>
   */
  @Override
  public void sendEmail(Email email) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        // Noop: Use @EnumConstant instead of check that the bizkey is correct

        // Check whether the email is the current person's own email
        checkUserEmail(email.getToAddrData().iterator().next(), email.getBizKey());
      }

      @Override
      protected Void process() {
        emailCmd.send(email, false);
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Validates email verification code and generates link secret.
   * </p>
   * <p>
   * Checks verification code correctness and user ownership. Generates and stores link secret for
   * binding confirmation.
   * </p>
   */
  @Override
  public String checkEmail(EmailBizKey bizKey, String email, String verificationCode) {
    return new BizTemplate<String>() {
      final Long userId = getUserId();

      @Override
      protected void checkParams() {
        // Noop: Use @EnumConstant instead of check that the bizkey is correct

        // Check whether the email is the current person's own email
        checkUserEmail(email, bizKey);

        // Check the email verification code is correct
        emailCmd.checkVerificationCode(bizKey, email, verificationCode);

        // Check the modified email exists
        userQuery.checkUpdateEmail(email, userId);
      }

      @Override
      protected String process() {
        String linkSecret = randomAlphabetic(MAX_LINK_SECRET_LENGTH);
        stringRedisService.set(String.format(CACHE_EMAIL_CHECK_SECRET_PREFIX, bizKey, userId),
            linkSecret, DEFAULT_SECRET_VALID_SECOND, TimeUnit.SECONDS);
        return linkSecret;
      }
    }.execute();
  }

  /**
   * <p>
   * Validates mobile number ownership for current user.
   * </p>
   * <p>
   * Checks if mobile belongs to current user or is available for binding. Throws appropriate
   * exception for unauthorized access.
   * </p>
   */
  private void checkUserMobile(String mobile, SmsBizKey bizKey) {
    List<User> users = userRepo.findByMobile(mobile);
    if (BIND_MOBILE.equals(bizKey)) {
      ProtocolAssert.assertTrue(isEmpty(users), MOBILE_BIND_EXISTED);
    } else {
      ProtocolAssert.assertTrue(isEmpty(users)
              || users.get(0).getId().equals(PrincipalContext.getUserId()),
          SEND_MOBILE_ERROR_T, new Object[]{mobile});
    }
  }

  /**
   * <p>
   * Validates email ownership for current user.
   * </p>
   * <p>
   * Checks if email belongs to current user or is available for binding. Throws appropriate
   * exception for unauthorized access.
   * </p>
   */
  private void checkUserEmail(String email, EmailBizKey bizKey) {
    List<User> users = userRepo.findByEmail(email);
    if (BIND_EMAIL.equals(bizKey)) {
      ProtocolAssert.assertTrue(isEmpty(users), EMAIL_BIND_EXISTED);
    } else {
      ProtocolAssert.assertTrue(isEmpty(users)
              || users.get(0).getId().equals(PrincipalContext.getUserId()),
          SEND_EMAIL_ERROR_T, new Object[]{email});
    }
  }

}
