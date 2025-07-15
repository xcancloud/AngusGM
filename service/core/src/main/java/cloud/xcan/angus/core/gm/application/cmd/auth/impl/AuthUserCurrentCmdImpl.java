package cloud.xcan.angus.core.gm.application.cmd.auth.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.gm.domain.AuthMessage.OLD_PASSWORD_ERROR;
import static cloud.xcan.angus.core.gm.domain.AuthMessage.PASSWORD_CANNOT_SAME;
import static cloud.xcan.angus.core.gm.domain.AuthMessage.PASSWORD_HAS_BEEN_INITIALIZED;
import static cloud.xcan.angus.core.gm.domain.AuthMessage.SIGN_IN_PASSWORD_ERROR;
import static cloud.xcan.angus.core.utils.CoreUtils.calcPasswordStrength;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isBlank;

import cloud.xcan.angus.api.commonlink.authuser.AuthUser;
import cloud.xcan.angus.api.commonlink.authuser.AuthUserRepo;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.cmd.auth.AuthUserCurrentCmd;
import cloud.xcan.angus.core.gm.application.query.auth.AuthUserQuery;
import jakarta.annotation.Resource;
import java.time.Instant;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Biz
public class AuthUserCurrentCmdImpl implements AuthUserCurrentCmd {

  @Resource
  private AuthUserRepo authUserRepo;

  @Resource
  private AuthUserQuery authUserQuery;

  @Resource
  private PasswordEncoder passwordEncoder;

  @Transactional(rollbackFor = {Exception.class})
  @Override
  public void updateCurrentPassword(String oldPassword, String newPassword) {
    new BizTemplate<Void>() {
      AuthUser userDb = null;

      @Override
      protected void checkParams() {
        // Check the new password is not the same as the old password
        assertTrue(!newPassword.equals(oldPassword), PASSWORD_CANNOT_SAME);

        // Check the old password is correct
        userDb = authUserQuery.checkAndFind(getUserId());
        assertTrue(passwordEncoder.matches(oldPassword, userDb.getPassword()), OLD_PASSWORD_ERROR);
      }

      @Override
      protected Void process() {
        userDb.setPassword(passwordEncoder.encode(newPassword));
        userDb.setPasswordStrength(calcPasswordStrength(newPassword).getValue());
        userDb.setLastModifiedPasswordDate(Instant.now());
        authUserRepo.save(userDb);
        return null;
      }
    }.execute();
  }

  @Override
  public void checkCurrentPassword(String password) {
    new BizTemplate<Void>() {
      AuthUser userDb;

      @Override
      protected void checkParams() {
        userDb = authUserQuery.checkAndFind(getUserId());
      }

      @Override
      protected Void process() {
        assertTrue(passwordEncoder.matches(password, userDb.getPassword()), SIGN_IN_PASSWORD_ERROR);
        return null;
      }
    }.execute();
  }

  @Override
  public void initCurrentPassword(String newPassword) {
    new BizTemplate<Void>() {
      AuthUser userDb;

      @Override
      protected void checkParams() {
        userDb = authUserQuery.checkAndFind(getUserId());
        assertTrue(isBlank(userDb.getPassword()), PASSWORD_HAS_BEEN_INITIALIZED);
      }

      @Override
      protected Void process() {
        userDb.setPassword(passwordEncoder.encode(newPassword));
        userDb.setPasswordStrength(calcPasswordStrength(newPassword).getValue());
        //userDb.setLastModifiedPasswordDate(Instant.now());
        authUserRepo.save(userDb);
        return null;
      }
    }.execute();
  }
}
