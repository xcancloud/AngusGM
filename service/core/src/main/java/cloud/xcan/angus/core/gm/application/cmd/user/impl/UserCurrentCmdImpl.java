package cloud.xcan.angus.core.gm.application.cmd.user.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceExisted;
import static cloud.xcan.angus.core.utils.CoreUtils.copyPropertiesIgnoreNull;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.authuser.AuthUser;
import cloud.xcan.angus.api.commonlink.email.EmailBizKey;
import cloud.xcan.angus.api.commonlink.sms.SmsBizKey;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.commonlink.user.UserRepo;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.cmd.authuser.AuthUserCmd;
import cloud.xcan.angus.core.gm.application.cmd.email.EmailCmd;
import cloud.xcan.angus.core.gm.application.cmd.sms.SmsCmd;
import cloud.xcan.angus.core.gm.application.cmd.user.UserCurrentCmd;
import cloud.xcan.angus.core.gm.application.query.authuser.AuthUserQuery;
import cloud.xcan.angus.core.gm.application.query.user.UserQuery;
import cloud.xcan.angus.core.utils.ValidatorUtils;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;


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

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void updateCurrent(User user) {
    new BizTemplate<Void>() {
      User userDb;

      @Override
      protected void checkParams() {
        // Check the user exists
        userDb = userQuery.checkAndFind(getUserId());
        // Check the username is not duplicate
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

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void mobileUpdate(String mobile, String country, String itc, String verificationCode,
      String linkSecret, SmsBizKey bizKey) {
    new BizTemplate<Void>() {
      final Long userId = getUserId();
      User userDb;

      @Override
      protected void checkParams() {
        // Check the mobile format
        ValidatorUtils.isMobile(mobile, country);
        // Check the sms link secret
        authUserQuery.checkSmsLinkSecret(userId, linkSecret, bizKey);
        // Check the verification code
        smsCmd.checkVerificationCode(bizKey, mobile, verificationCode);
        //Check the same mobile exists under the tenant
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

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void updateEmail(String email, String verificationCode, String linkSecret,
      EmailBizKey bizKey) {
    new BizTemplate<Void>() {
      final Long userId = getUserId();
      User userDb;

      @Override
      protected void checkParams() {
        // Check the sms link secret
        authUserQuery.checkLinkSecret(userId, linkSecret, bizKey);
        // Check the verification code
        emailCmd.checkVerificationCode(bizKey, email, verificationCode);
        //Check the same mobile exists under the tenant
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
