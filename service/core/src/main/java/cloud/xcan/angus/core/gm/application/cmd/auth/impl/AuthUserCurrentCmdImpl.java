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

/**
 * Implementation of current user authentication command operations.
 * 
 * <p>This class provides comprehensive functionality for current user authentication management including:</p>
 * <ul>
 *   <li>Updating current user password with validation</li>
 *   <li>Verifying current user password</li>
 *   <li>Initializing current user password</li>
 *   <li>Managing password strength and security settings</li>
 * </ul>
 * 
 * <p>The implementation ensures secure password management for the currently
 * authenticated user with proper validation and security checks.</p>
 */
@Biz
public class AuthUserCurrentCmdImpl implements AuthUserCurrentCmd {

  @Resource
  private AuthUserRepo authUserRepo;
  @Resource
  private AuthUserQuery authUserQuery;
  @Resource
  private PasswordEncoder passwordEncoder;

  /**
   * Updates current user password with comprehensive validation.
   * 
   * <p>This method performs password update including:</p>
   * <ul>
   *   <li>Validating that new password differs from old password</li>
   *   <li>Verifying current password correctness</li>
   *   <li>Encoding new password securely</li>
   *   <li>Updating password strength and modification date</li>
   * </ul>
   * 
   * @param oldPassword Current password for verification
   * @param newPassword New password to set
   */
  @Transactional(rollbackFor = {Exception.class})
  @Override
  public void updateCurrentPassword(String oldPassword, String newPassword) {
    new BizTemplate<Void>() {
      AuthUser userDb = null;

      @Override
      protected void checkParams() {
        // Ensure new password is different from old password
        assertTrue(!newPassword.equals(oldPassword), PASSWORD_CANNOT_SAME);

        // Validate current password is correct
        userDb = authUserQuery.checkAndFind(getUserId());
        assertTrue(passwordEncoder.matches(oldPassword, userDb.getPassword()), OLD_PASSWORD_ERROR);
      }

      @Override
      protected Void process() {
        // Update password with secure encoding
        userDb.setPassword(passwordEncoder.encode(newPassword));
        // Calculate and update password strength
        userDb.setPasswordStrength(calcPasswordStrength(newPassword).getValue());
        // Update password modification date
        userDb.setLastModifiedPasswordDate(Instant.now());
        authUserRepo.save(userDb);
        return null;
      }
    }.execute();
  }

  /**
   * Verifies current user password for authentication purposes.
   * 
   * <p>This method validates the provided password against the current user's
   * stored password for authentication verification.</p>
   * 
   * @param password Password to verify
   */
  @Override
  public void checkCurrentPassword(String password) {
    new BizTemplate<Void>() {
      AuthUser userDb;

      @Override
      protected void checkParams() {
        // Retrieve current user information
        userDb = authUserQuery.checkAndFind(getUserId());
      }

      @Override
      protected Void process() {
        // Verify password matches stored password
        assertTrue(passwordEncoder.matches(password, userDb.getPassword()), SIGN_IN_PASSWORD_ERROR);
        return null;
      }
    }.execute();
  }

  /**
   * Initializes current user password for first-time setup.
   * 
   * <p>This method sets up the initial password for a user who has not
   * yet set their password, typically after account creation.</p>
   * 
   * @param newPassword New password to initialize
   */
  @Override
  public void initCurrentPassword(String newPassword) {
    new BizTemplate<Void>() {
      AuthUser userDb;

      @Override
      protected void checkParams() {
        // Retrieve current user information
        userDb = authUserQuery.checkAndFind(getUserId());
        // Ensure password has not been initialized before
        assertTrue(isBlank(userDb.getPassword()), PASSWORD_HAS_BEEN_INITIALIZED);
      }

      @Override
      protected Void process() {
        // Set initial password with secure encoding
        userDb.setPassword(passwordEncoder.encode(newPassword));
        // Calculate and update password strength
        userDb.setPasswordStrength(calcPasswordStrength(newPassword).getValue());
        // Note: Last modified date is not set for initial password
        // userDb.setLastModifiedPasswordDate(Instant.now());
        authUserRepo.save(userDb);
        return null;
      }
    }.execute();
  }
}
