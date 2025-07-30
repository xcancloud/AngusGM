package cloud.xcan.angus.core.gm.application.cmd.auth.impl;


import static cloud.xcan.angus.core.biz.ProtocolAssert.assertForbidden;
import static cloud.xcan.angus.core.gm.application.converter.UserConverter.replaceToAuthUser;
import static cloud.xcan.angus.core.utils.CoreUtils.calcPasswordStrength;
import static cloud.xcan.angus.core.utils.CoreUtils.copyPropertiesIgnoreNull;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.setMultiTenantCtrl;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import cloud.xcan.angus.api.commonlink.AuthOrgType;
import cloud.xcan.angus.api.commonlink.authuser.AuthUser;
import cloud.xcan.angus.api.commonlink.authuser.AuthUserRepo;
import cloud.xcan.angus.api.commonlink.tenant.Tenant;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.enums.PasswordStrength;
import cloud.xcan.angus.api.enums.TenantRealNameStatus;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.auth.AuthUserCmd;
import cloud.xcan.angus.core.gm.application.cmd.auth.AuthUserTokenCmd;
import cloud.xcan.angus.core.gm.application.cmd.policy.AuthPolicyTenantCmd;
import cloud.xcan.angus.core.gm.application.cmd.to.TOUserCmd;
import cloud.xcan.angus.core.gm.application.query.auth.AuthUserQuery;
import cloud.xcan.angus.core.gm.application.query.auth.AuthUserSignQuery;
import cloud.xcan.angus.core.gm.application.query.tenant.TenantQuery;
import cloud.xcan.angus.core.gm.domain.policy.org.AuthPolicyOrgRepo;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.spring.boot.ApplicationInfo;
import cloud.xcan.angus.security.authentication.service.JdbcOAuth2AuthorizationService;
import jakarta.annotation.Resource;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of authentication user command operations for managing user authentication.
 * 
 * <p>This class provides comprehensive functionality for user authentication management including:</p>
 * <ul>
 *   <li>Creating and updating authentication users</li>
 *   <li>Managing user passwords and security settings</li>
 *   <li>Handling tenant real-name verification status</li>
 *   <li>Deleting users and cleaning up related data</li>
 *   <li>Managing user authorization records</li>
 * </ul>
 * 
 * <p>The implementation ensures proper user authentication lifecycle management
 * and maintains consistency with user management services.</p>
 */
@Biz
@Slf4j
public class AuthUserCmdImpl extends CommCmd<AuthUser, Long> implements AuthUserCmd {

  @Resource
  private AuthUserRepo authUserRepo;
  @Resource
  private AuthUserQuery authUserQuery;
  @Resource
  private AuthUserSignQuery authUserSignQuery;
  @Resource
  private AuthUserTokenCmd authUserTokenCmd;
  @Resource
  private TOUserCmd toUserCmd;
  @Resource
  private PasswordEncoder passwordEncoder;
  @Resource
  private AuthPolicyTenantCmd authPolicyTenantCmd;
  @Resource
  private AuthPolicyOrgRepo authPolicyOrgRepo;
  @Resource
  private JdbcOAuth2AuthorizationService auth2AuthorizationService;
  @Resource
  private TenantQuery tenantQuery;
  @Resource
  private ApplicationInfo applicationInfo;

  /**
   * Replaces authentication user with user data and password.
   * 
   * <p>This method handles user replacement including:</p>
   * <ul>
   *   <li>Converting user data to authentication user format</li>
   *   <li>Setting up tenant information</li>
   *   <li>Initializing tenant if requested</li>
   * </ul>
   * 
   * @param userDb User entity from user management service
   * @param password User password for authentication
   * @param initTenant Whether to initialize tenant
   */
  @Override
  public void replaceAuthUser(User userDb, String password, boolean initTenant){
    Tenant tenantDb = tenantQuery.checkAndFind(userDb.getTenantId());
    replace0(replaceToAuthUser(userDb, password, tenantDb), initTenant);
  }

  /**
   * Replaces authentication user with comprehensive setup.
   * 
   * <p>This method performs comprehensive user replacement including:</p>
   * <ul>
   *   <li>Password encoding and validation</li>
   *   <li>Multi-tenant control setup</li>
   *   <li>User creation or update</li>
   *   <li>Tenant authorization policy initialization</li>
   * </ul>
   * 
   * @param user Authentication user entity
   * @param initTenant Whether to initialize tenant
   */
  @Override
  public void replace0(AuthUser user, Boolean initTenant) {
    Long tenantId = Long.valueOf(user.getTenantId());

    if (initTenant) {
      setMultiTenantCtrl(false);
    }

    // Encode password if provided
    if (isNotBlank(user.getPassword())) {
      // Note: Password may already be encoded from LDAP
      user.setPassword(user.getPassword().startsWith("{") ? user.getPassword()
          : passwordEncoder.encode(user.getPassword()));
      if (!initTenant) {
        // Validate password length against tenant settings
        authUserSignQuery.checkMinPasswordLengthByTenantSetting(tenantId, user.getPassword());
      }
    }

    // Find existing authentication user
    AuthUser userDb = authUserRepo.findById(Long.valueOf(user.getId())).orElse(null);

    // Note: Adding and modifying at the same time is not supported

    // Create new authentication user
    if (isEmpty(userDb)) {
      insert(user);
    } else {
      // Update existing authentication user
      authUserRepo.save(copyPropertiesIgnoreNull(user, userDb));
    }

    // Initialize global management open authorization and default policy for cloud service
    if (initTenant && applicationInfo.isCloudServiceEdition()) {
      authPolicyTenantCmd.intAndOpenAppByTenantWhenSignup(tenantId);
    }
  }

  /**
   * Deletes users and cleans up related data.
   * 
   * <p>Note: User deletion must be initiated from the UC service.
   * This method performs comprehensive cleanup including:</p>
   * <ul>
   *   <li>Deleting user tokens</li>
   *   <li>Removing organization authorization policies</li>
   *   <li>Cleaning up authentication user records</li>
   * </ul>
   * 
   * @param ids Set of user identifiers to delete
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(Set<Long> ids) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        if (isNotEmpty(ids)) {
          // Delete user from user management service
          toUserCmd.delete0(ids);
          // Delete user tokens
          authUserTokenCmd.delete(ids);
          // Remove organization authorization policies
          authPolicyOrgRepo.deleteByOrgIdInAndOrgType(ids, AuthOrgType.USER.getValue());
          // Delete authentication user records
          authUserRepo.deleteByIdIn(ids.stream().map(Object::toString).collect(Collectors.toSet()));
        }
        return null;
      }
    }.execute();
  }

  /**
   * Updates user password with validation and security checks.
   * 
   * <p>This method performs password update including:</p>
   * <ul>
   *   <li>Validating user existence</li>
   *   <li>Checking password length requirements</li>
   *   <li>Enforcing security restrictions for system administrators</li>
   *   <li>Updating password strength and modification date</li>
   * </ul>
   * 
   * @param id User identifier
   * @param newPassword New password for user
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void passwordUpdate(Long id, String newPassword) {
    new BizTemplate<Void>() {
      AuthUser userDb;

      @Override
      protected void checkParams() {
        // Validate that user exists
        userDb = authUserQuery.checkAndFind(id);
        // Validate password length against tenant settings
        authUserSignQuery.checkMinPasswordLengthByTenantSetting(Long.parseLong(userDb.getTenantId()),
            newPassword);
        // Prevent modification of signup tenant system administrator password
        assertForbidden(!userDb.isSysAdmin() || userDb.getId().equals(getUserId().toString()),
            "Forbidden update password of signup tenant system administrator");
      }

      @Override
      protected Void process() {
        // Update password with encoding
        userDb.setPassword(passwordEncoder.encode(newPassword));
        // Calculate and update password strength
        PasswordStrength passwordStrength = calcPasswordStrength(newPassword);
        userDb.setPasswordStrength(passwordStrength.getValue());
        // Update password modification date
        userDb.setLastModifiedPasswordDate(Instant.now());
        authUserRepo.save(userDb);
        return null;
      }
    }.execute();
  }

  /**
   * Updates tenant real-name verification status.
   * 
   * <p>This method updates the real-name verification status for all users
   * within a specific tenant.</p>
   * 
   * @param tenantId Tenant identifier
   * @param realNameStatus Real-name verification status to set
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void realName(Long tenantId, TenantRealNameStatus realNameStatus) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        // Update real-name status for all users in tenant
        authUserRepo.updateStatusByTenantId(tenantId.toString(), realNameStatus.getValue());
        return null;
      }
    }.execute();
  }

  /**
   * Deletes authorization records for specified principals.
   * 
   * <p>This method removes OAuth2 authorization records for the specified
   * principal names from the authorization service.</p>
   * 
   * @param principalNames List of principal names to remove authorizations for
   */
  @Override
  public void deleteAuthorization(List<String> principalNames){
    auth2AuthorizationService.removeByPrincipalName(principalNames);
  }

  @Override
  protected BaseRepository<AuthUser, Long> getRepository() {
    return this.authUserRepo;
  }
}
