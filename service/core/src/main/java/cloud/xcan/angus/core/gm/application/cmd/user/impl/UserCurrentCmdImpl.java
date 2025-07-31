package cloud.xcan.angus.core.gm.application.cmd.user.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceExisted;
import static cloud.xcan.angus.core.utils.CoreUtils.copyPropertiesIgnoreNull;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.email.EmailBizKey;
import cloud.xcan.angus.api.commonlink.sms.SmsBizKey;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.commonlink.user.UserRepo;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.cmd.auth.AuthUserCmd;
import cloud.xcan.angus.core.gm.application.cmd.email.EmailCmd;
import cloud.xcan.angus.core.gm.application.cmd.sms.SmsCmd;
import cloud.xcan.angus.core.gm.application.cmd.user.UserCurrentCmd;
import cloud.xcan.angus.core.gm.application.query.auth.AuthUserQuery;
import cloud.xcan.angus.core.gm.application.query.user.UserQuery;
import cloud.xcan.angus.core.utils.ValidatorUtils;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * Implementation of current user command operations.
 * </p>
 * <p>
 * Manages current user profile updates including basic information, mobile number,
 * and email address changes with verification.
 * </p>
 * <p>
 * Supports secure profile updates with SMS and email verification for sensitive
 * information changes.
 * </p>
 */
@Biz
public class UserCurrentCmdImpl implements UserCurrentCmd {

  @Resource
  private UserRepo userRepo;
  @Resource
  private UserQuery userQuery;
  @Resource
  private AuthUserQuery authUserQuery;
  @Resource
  private AuthUserCmd authUserCmd;
  @Resource
  private SmsCmd smsCmd;
  @Resource
  private EmailCmd emailCmd;

  /**
   * <p>
   * Updates current user profile information.
   * </p>
   * <p>
   * Validates user existence and username uniqueness, updates user information,
   * and refreshes OAuth2 user data.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void updateCurrent(User user) {
    new BizTemplate<Void>() {
      User userDb;

      @Override
      protected void checkParams() {
        // Verify user exists
        userDb = userQuery.checkAndFind(getUserId());
        // Verify username uniqueness
        if (isNotEmpty(user.getUsername()) && !user.getUsername().equals(userDb.getUsername())) {
          userQuery.checkUsernameUpdate(user.getUsername(), userDb.getId());
        }
      }

      @Override
      protected Void process() {
        userRepo.save(copyPropertiesIgnoreNull(user, userDb));

        authUserCmd.replaceAuthUser(userDb, null, false);
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Updates current user mobile number with verification.
   * </p>
   * <p>
   * Validates mobile format, SMS link secret, verification code, and mobile uniqueness.
   * Updates user mobile information and refreshes OAuth2 user data.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void mobileUpdate(String mobile, String country, String itc, String verificationCode,
      String linkSecret, SmsBizKey bizKey) {
    new BizTemplate<Void>() {
      final Long userId = getUserId();
      User userDb;

      @Override
      protected void checkParams() {
        // Verify mobile format
        ValidatorUtils.isMobile(mobile, country);
        // Verify SMS link secret
        authUserQuery.checkSmsLinkSecret(userId, linkSecret, bizKey);
        // Verify verification code
        smsCmd.checkVerificationCode(bizKey, mobile, verificationCode);
        // Verify mobile uniqueness within tenant
        List<User> users = userRepo.findByMobile(mobile);
        assertResourceExisted(isEmpty(users)
            || users.get(0).getId().equals(userId), mobile, "User");
        userDb = userQuery.checkAndFind(userId);
      }

      @Override
      protected Void process() {
        userDb.setMobile(mobile).setItc(itc).setCountry(country);
        userRepo.save(userDb);

        authUserCmd.replaceAuthUser(userDb, null, false);
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Updates current user email address with verification.
   * </p>
   * <p>
   * Validates email link secret, verification code, and email uniqueness.
   * Updates user email information and refreshes OAuth2 user data.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void updateEmail(String email, String verificationCode, String linkSecret,
      EmailBizKey bizKey) {
    new BizTemplate<Void>() {
      final Long userId = getUserId();
      User userDb;

      @Override
      protected void checkParams() {
        // Verify email link secret
        authUserQuery.checkLinkSecret(userId, linkSecret, bizKey);
        // Verify verification code
        emailCmd.checkVerificationCode(bizKey, email, verificationCode);
        // Verify email uniqueness within tenant
        List<User> users = userRepo.findByEmail(email);
        assertResourceExisted(isEmpty(users)
            || users.get(0).getId().equals(userId), email, "User");
        userDb = userQuery.checkAndFind(userId);
      }

      @Override
      protected Void process() {
        userDb.setEmail(email);
        userRepo.save(userDb);

        authUserCmd.replaceAuthUser(userDb, null, false);
        return null;
      }
    }.execute();
  }

}
