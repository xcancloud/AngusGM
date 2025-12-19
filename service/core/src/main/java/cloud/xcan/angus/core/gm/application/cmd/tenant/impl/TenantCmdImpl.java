package cloud.xcan.angus.core.gm.application.cmd.tenant.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.gm.application.converter.TenantConverter.assembleTenantInfo;
import static cloud.xcan.angus.core.gm.domain.UserMessage.TENANT_CERT_MISSING_T;
import static cloud.xcan.angus.core.gm.domain.operation.OperationResourceType.TENANT;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.CREATED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.DISABLED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.ENABLED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.LOCKED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.UNLOCKED;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.UPDATED;
import static cloud.xcan.angus.core.utils.CoreUtils.copyPropertiesIgnoreNull;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.setOptTenantId;
import static cloud.xcan.angus.spec.utils.DateUtils.DATE_TIME_FMT;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.tenant.Tenant;
import cloud.xcan.angus.api.commonlink.tenant.TenantRepo;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.enums.TenantRealNameStatus;
import cloud.xcan.angus.api.enums.TenantStatus;
import cloud.xcan.angus.api.enums.UserSource;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.operation.OperationLogCmd;
import cloud.xcan.angus.core.gm.application.cmd.tenant.TenantCertAuditCmd;
import cloud.xcan.angus.core.gm.application.cmd.tenant.TenantCmd;
import cloud.xcan.angus.core.gm.application.cmd.user.UserCmd;
import cloud.xcan.angus.core.gm.application.query.tenant.TenantQuery;
import cloud.xcan.angus.core.gm.application.query.user.UserQuery;
import cloud.xcan.angus.core.gm.domain.tenant.audit.TenantCertAudit;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * Implementation of tenant command operations.
 * </p>
 * <p>
 * Manages tenant lifecycle including creation, updates, status management, and lock/unlock
 * operations.
 * </p>
 * <p>
 * Supports tenant creation with certificate audit, status management, and scheduled lock/unlock
 * operations via job processing.
 * </p>
 */
@org.springframework.stereotype.Service
@Slf4j
public class TenantCmdImpl extends CommCmd<Tenant, Long> implements TenantCmd {

  @Resource
  private TenantRepo tenantRepo;
  @Resource
  private TenantQuery tenantQuery;
  @Resource
  private TenantCertAuditCmd tenantCertAuditCmd;
  @Resource
  private UserQuery userQuery;
  @Resource
  private UserCmd userCmd;
  @Resource
  private OperationLogCmd operationLogCmd;

  /**
   * <p>
   * Creates a new tenant with certificate audit and system administrator.
   * </p>
   * <p>
   * Creates tenant, submits certificate audit if provided, and creates system administrator user
   * for the tenant.
   * </p>
   * <p>
   * Allows tenants to submit their own certificates after creation.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public IdKey<Long, Object> add(Tenant tenant, TenantCertAudit audit,
      User user, UserSource userSource) {
    return new BizTemplate<IdKey<Long, Object>>() {

      @Override
      protected IdKey<Long, Object> process() {
        // Save tenant with real-name status
        if (!audit.isCertSubmitted()) {
          tenant.setRealNameStatus(TenantRealNameStatus.NOT_SUBMITTED)
              .setType(audit.getType());
        }
        IdKey<Long, Object> idKey = insert(tenant, "name");

        // Submit certificate audit if provided
        Long tenantId = tenant.getId();
        setOptTenantId(tenantId);
        if (audit.isCertSubmitted()) {
          audit.setTenantId(tenantId);
          tenantCertAuditCmd.submit(audit);
        }

        // Create system administrator user for tenant
        user.setTenantId(tenantId);
        user.setTenantName(tenant.getName());
        setOptTenantId(tenantId);
        userCmd.add(user, emptyList(), emptyList(), emptyList(), userSource);

        // Log operation for audit
        operationLogCmd.add(TENANT, tenant, CREATED);
        return idKey;
      }
    }.execute();
  }

  /**
   * <p>
   * Updates tenant information and related data.
   * </p>
   * <p>
   * Updates tenant details, certificate audit if applicable, and user information. Prevents name
   * modification after real-name authentication is passed.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void update(Tenant tenant, TenantCertAudit audit, User user) {
    new BizTemplate<Void>() {
      Tenant tenantDb;

      @Override
      protected void checkParams() {
        // Verify tenant is not canceled
        tenantDb = tenantQuery.checkAndFind(tenant.getId());
        tenantQuery.checkTenantCanceled(tenantDb);
      }

      @Override
      protected Void process() {
        // Update tenant information
        setOptTenantId(tenant.getId());
        if (tenantDb.isRealNamePassed()) {
          tenant.setName(null); // Cannot modify name after real-name authentication
        }
        tenantRepo.save(copyPropertiesIgnoreNull(tenant, tenantDb));

        // Submit certificate audit if applicable
        if (nonNull(audit) && !tenantDb.isRealNamePassed() && tenantDb.isRealNameAuditing()) {
          audit.setId(tenant.getId()).setTenantId(tenant.getId());
          tenantCertAuditCmd.submit(audit);
        }

        if (nonNull(user)) {
          // Update user information
          User updateUserDb = userQuery.findSignupOrFirstSysAdminUser(tenant.getId());
          user.setId(updateUserDb.getId());
          userCmd.update(user, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        }

        // Log operation for audit
        operationLogCmd.add(TENANT, tenant, UPDATED);
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Replaces tenant information completely.
   * </p>
   * <p>
   * Replaces tenant details, certificate audit if applicable, and user information. Validates
   * certificate submission requirements.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void replace(Tenant tenant, TenantCertAudit audit, User user) {
    new BizTemplate<Void>() {
      Tenant tenantDb;

      @Override
      protected void checkParams() {
        setOptTenantId(tenant.getId());

        // Verify tenant is not canceled
        tenantDb = tenantQuery.checkAndFind(tenant.getId());
        tenantQuery.checkTenantCanceled(tenantDb);

        // Verify certificate is required when applicable
        if (nonNull(audit) && !tenantDb.isRealNamePassed() && tenantDb.isRealNameAuditing()) {
          assertTrue(audit.isCertSubmitted(), TENANT_CERT_MISSING_T,
              new Object[]{nonNull(audit.getType()) ? audit.getType() : null});
        }
      }

      @Override
      protected Void process() {
        // Replace tenant information
        assembleTenantInfo(tenantDb, tenant);
        tenantRepo.save(tenantDb);

        // Submit certificate audit if applicable
        if (nonNull(audit) && !tenantDb.isRealNamePassed() && tenantDb.isRealNameAuditing()) {
          audit.setId(tenant.getId()).setTenantId(tenant.getId());
          tenantCertAuditCmd.submit(audit);
        }
        // Replace user information
        User updateUserDb = userQuery.findSignupOrFirstSysAdminUser(tenant.getId());
        user.setId(updateUserDb.getId());
        userCmd.replace(user, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        // Log operation for audit
        operationLogCmd.add(TENANT, tenantDb, UPDATED);
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Enables or disables tenant.
   * </p>
   * <p>
   * Updates tenant status and logs the operation for audit purposes.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void enabled(Long id, Boolean enabled) {
    new BizTemplate<Void>() {
      Tenant tenantDb;

      @Override
      protected void checkParams() {
        // Verify tenant exists
        tenantDb = tenantQuery.checkAndFind(id);

        // Verify tenant is not canceled
        tenantQuery.checkTenantCanceled(tenantDb);
      }

      @Override
      protected Void process() {
        tenantDb.setStatus(enabled ? TenantStatus.ENABLED : TenantStatus.DISABLED);
        tenantRepo.save(tenantDb);

        // Log operation for audit
        operationLogCmd.add(TENANT, tenantDb, enabled ? ENABLED : DISABLED);
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Locks or unlocks tenant with optional date constraints.
   * </p>
   * <p>
   * Sets lock status with start and end dates, validates date constraints, and logs the operation
   * for audit purposes.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void locked(Long id, Boolean locked, LocalDateTime lockStartDate,
      LocalDateTime lockEndDate) {
    new BizTemplate<Void>() {
      Tenant tenantDb;

      @Override
      protected void checkParams() {
        // Verify operation permissions when user action

        // Verify date constraints are valid
        assertTrue(!locked || isNull(lockStartDate)
                || isNull(lockEndDate) || lockEndDate.isAfter(lockStartDate),
            String.format("lockEndDate[%s] is not after lockStartDate[%s]",
                nonNull(lockEndDate) ? lockEndDate.format(DATE_TIME_FMT) : null,
                nonNull(lockStartDate) ? lockStartDate.format(DATE_TIME_FMT) : null));
        assertTrue(!locked || isNull(lockEndDate) // Ignore warning??
                || lockEndDate.isAfter(LocalDateTime.now()),
            String.format("lockEndDate[%s] must be a future date",
                nonNull(lockEndDate) ? lockEndDate.format(DATE_TIME_FMT) : null));

        // Verify tenant exists
        tenantDb = tenantQuery.checkAndFind(id);
      }

      @Override
      protected Void process() {
        if (locked) {
          tenantDb.setLocked((isNull(lockStartDate) && isNull(lockEndDate))
                  || (nonNull(lockStartDate) && lockStartDate
                  .isBefore(LocalDateTime.now().minusSeconds(30))))
              .setLockStartDate(lockStartDate).setLockEndDate(lockEndDate);
        } else {
          tenantDb.setLocked(false).setLockStartDate(null).setLockEndDate(null);
        }
        tenantRepo.save(tenantDb);

        // Log operation for audit
        operationLogCmd.add(TENANT, tenantDb, locked ? LOCKED : UNLOCKED);
        return null;
      }

    }.execute();
  }

  /**
   * <p>
   * Processes expired tenant locks.
   * </p>
   * <p>
   * Used by TenantLockedJob to automatically lock tenants when lock conditions are met.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void lockExpire(Set<Long> tenantIds) {
    new BizTemplate<Void>() {

      @Override
      protected Void process() {
        if (isNotEmpty(tenantIds)) {
          tenantRepo.updateLockStatusByIdIn(tenantIds);
        }
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Processes expired tenant unlocks.
   * </p>
   * <p>
   * Used by TenantUnlockedJob to automatically unlock tenants when unlock conditions are met.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void unlockExpire(Set<Long> tenantIds) {
    new BizTemplate<Void>() {

      @Override
      protected Void process() {
        if (isNotEmpty(tenantIds)) {
          tenantRepo.updateUnlockStatusByIdIn(tenantIds);
        }
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Creates tenant without additional processing.
   * </p>
   * <p>
   * Basic tenant creation without certificate audit or user creation.
   * </p>
   */
  @Override
  public IdKey<Long, Object> add0(Tenant tenant) {
    return insert(tenant, "name");
  }

  @Override
  protected BaseRepository<Tenant, Long> getRepository() {
    return this.tenantRepo;
  }
}
