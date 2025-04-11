package cloud.xcan.angus.core.gm.application.cmd.authuser.impl;


import static cloud.xcan.angus.core.biz.ProtocolAssert.assertForbidden;
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
import cloud.xcan.angus.api.commonlink.tenant.TenantRealNameStatus;
import cloud.xcan.angus.api.enums.PasswordStrength;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.authuser.AuthUserCmd;
import cloud.xcan.angus.core.gm.application.cmd.authuser.AuthUserTokenCmd;
import cloud.xcan.angus.core.gm.application.cmd.policy.AuthPolicyTenantCmd;
import cloud.xcan.angus.core.gm.application.cmd.to.TOUserCmd;
import cloud.xcan.angus.core.gm.application.query.authuser.AuthUserQuery;
import cloud.xcan.angus.core.gm.application.query.authuser.AuthUserSignQuery;
import cloud.xcan.angus.core.gm.domain.policy.org.AuthPolicyOrgRepo;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.spring.boot.ApplicationInfo;
import jakarta.annotation.Resource;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;


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
  private ApplicationInfo applicationInfo;

  @Override
  public void replace0(AuthUser user, Boolean initTenant) {
    Long tenantId = Long.valueOf(user.getTenantId());

    if (initTenant) {
      setMultiTenantCtrl(false);
    }

    // Encode password. Note: Password is nullable.
    if (isNotBlank(user.getPassword())) {
      // Fix:: The password has been encoded from LDAP
      user.setPassword(user.getPassword().startsWith("{") ? user.getPassword()
          : passwordEncoder.encode(user.getPassword()));
      if (!initTenant) {
        // Check password length
        authUserSignQuery.checkMinPasswordLengthByTenantSetting(tenantId, user.getPassword());
      }
    }

    // Find existed auth user
    AuthUser userDb = authUserRepo.findById(Long.valueOf(user.getId())).orElse(null);

    // Note:: Adding and modifying at the same time is not supported.

    // Add new auth user
    if (isEmpty(userDb)) {
      insert(user);
    } else {
      // Update auth user
      authUserRepo.save(copyPropertiesIgnoreNull(user, userDb));
    }

    // Initialize global management open authorization and default policy when cloud service
    if (initTenant && applicationInfo.isCloudServiceEdition()) {
      authPolicyTenantCmd.intAndOpenAppByTenantWhenSignup(tenantId);
    }
  }

  /**
   * Deleting users must be initiated from the UC service.
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(Set<Long> ids) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        if (isNotEmpty(ids)) {
          toUserCmd.delete0(ids);
          authUserTokenCmd.delete(ids);
          authPolicyOrgRepo.deleteByOrgIdInAndOrgType(ids, AuthOrgType.USER.getValue());
          authUserRepo.deleteByIdIn(ids.stream().map(Object::toString).collect(Collectors.toSet()));
        }
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void passwordUpdate(Long id, String newPassword) {
    new BizTemplate<Void>() {
      AuthUser userDb;

      @Override
      protected void checkParams() {
        // Check the user existed
        userDb = authUserQuery.checkAndFind(id);
        // Check the password length
        authUserSignQuery.checkMinPasswordLengthByTenantSetting(Long.parseLong(userDb.getTenantId()),
            newPassword);
        // Check the signup tenant system administrator password is not allowed to be modified
        assertForbidden(!userDb.isSysAdmin() || userDb.getId().equals(getUserId().toString()),
            "Forbidden update password of signup tenant system administrator");
      }

      @Override
      protected Void process() {
        userDb.setPassword(passwordEncoder.encode(newPassword));
        PasswordStrength passwordStrength = calcPasswordStrength(newPassword);
        userDb.setPasswordStrength(passwordStrength.getValue());
        userDb.setLastModifiedPasswordDate(Instant.now());
        authUserRepo.save(userDb);
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void realName(Long tenantId, TenantRealNameStatus realNameStatus) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        authUserRepo.updateStatusByTenantId(tenantId.toString(), realNameStatus.getValue());
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<AuthUser, Long> getRepository() {
    return this.authUserRepo;
  }
}
