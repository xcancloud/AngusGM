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
import cloud.xcan.angus.api.commonlink.tenant.TenantRealNameStatus;
import cloud.xcan.angus.api.commonlink.tenant.TenantRepo;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.enums.TenantStatus;
import cloud.xcan.angus.api.enums.UserSource;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.operation.OperationLogCmd;
import cloud.xcan.angus.core.gm.application.cmd.tenant.TenantCertAuditCmd;
import cloud.xcan.angus.core.gm.application.cmd.tenant.TenantCmd;
import cloud.xcan.angus.core.gm.application.cmd.user.UserCmd;
import cloud.xcan.angus.core.gm.application.query.tenant.TenantQuery;
import cloud.xcan.angus.core.gm.application.query.user.UserQuery;
import cloud.xcan.angus.core.gm.domain.tenant.audit.TenantCertAudit;
import cloud.xcan.angus.core.gm.infra.job.TenantLockedJob;
import cloud.xcan.angus.core.gm.infra.job.TenantUnlockedJob;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Biz
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
   * Allow tenants to submit their own cert after adding.
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public IdKey<Long, Object> add(Tenant tenant, TenantCertAudit audit,
      User user, UserSource userSource) {
    return new BizTemplate<IdKey<Long, Object>>() {

      @Override
      protected IdKey<Long, Object> process() {
        // Save tenant
        if (!audit.isCertSubmitted()) {
          tenant.setRealNameStatus(TenantRealNameStatus.NOT_SUBMITTED)
              .setType(audit.getType());
        }
        IdKey<Long, Object> idKey = insert(tenant, "name");

        // Save the pending audit of tenant
        Long tenantId = tenant.getId();
        setOptTenantId(tenantId);
        if (audit.isCertSubmitted()) {
          audit.setTenantId(tenantId);
          tenantCertAuditCmd.submit(audit);
        }

        // Save the system admin user of tenant
        user.setTenantId(tenantId);
        user.setTenantName(tenant.getName());
        setOptTenantId(tenantId);
        userCmd.add(user, emptyList(), emptyList(), emptyList(), userSource);

        // Save operation activity
        operationLogCmd.add(TENANT, tenant, CREATED);
        return idKey;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void update(Tenant tenant, TenantCertAudit audit, User user) {
    new BizTemplate<Void>() {
      Tenant tenantDb;

      @Override
      protected void checkParams() {
        // Check the canceled status
        tenantDb = tenantQuery.checkAndFind(tenant.getId());
        tenantQuery.checkTenantCanceled(tenantDb);
      }

      @Override
      protected Void process() {
        // Update tenant
        setOptTenantId(tenant.getId());
        if (tenantDb.isRealNamePassed()) {
          tenant.setName(null); // Cannot modify after real name
        }
        tenantRepo.save(copyPropertiesIgnoreNull(tenant, tenantDb));

        // Ignore the cert information modification when the real name audit has passed
        if (nonNull(audit) && !tenantDb.isRealNamePassed() && tenantDb.isRealNameAuditing()) {
          audit.setId(tenant.getId()).setTenantId(tenant.getId());
          tenantCertAuditCmd.submit(audit);
        }

        if (nonNull(user)) {
          // Update user
          User updateUserDb = userQuery.findSignupOrFirstSysAdminUser(tenant.getId());
          user.setId(updateUserDb.getId());
          userCmd.update(user, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        }

        // Save operation activity
        operationLogCmd.add(TENANT, tenant, UPDATED);
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void replace(Tenant tenant, TenantCertAudit audit, User user) {
    new BizTemplate<Void>() {
      Tenant tenantDb;

      @Override
      protected void checkParams() {
        setOptTenantId(tenant.getId());

        // Check the canceled status
        tenantDb = tenantQuery.checkAndFind(tenant.getId());
        tenantQuery.checkTenantCanceled(tenantDb);

        // Check the cert is required
        if (nonNull(audit) && !tenantDb.isRealNamePassed() && tenantDb.isRealNameAuditing()) {
          assertTrue(audit.isCertSubmitted(), TENANT_CERT_MISSING_T,
              new Object[]{nonNull(audit.getType()) ? audit.getType() : null});
        }
      }

      @Override
      protected Void process() {
        // Update tenant
        assembleTenantInfo(tenantDb, tenant);
        tenantRepo.save(tenantDb);

        // Ignore the cert information modification when the real name audit has passed
        if (nonNull(audit) && !tenantDb.isRealNamePassed() && tenantDb.isRealNameAuditing()) {
          audit.setId(tenant.getId()).setTenantId(tenant.getId());
          tenantCertAuditCmd.submit(audit);
        }
        // Replace user
        User updateUserDb = userQuery.findSignupOrFirstSysAdminUser(tenant.getId());
        user.setId(updateUserDb.getId());
        userCmd.replace(user, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        // Save operation activity
        operationLogCmd.add(TENANT, tenantDb, UPDATED);
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void enabled(Long id, Boolean enabled) {
    new BizTemplate<Void>() {
      Tenant tenantDb;

      @Override
      protected void checkParams() {
        // Check the tenant existed
        tenantDb = tenantQuery.checkAndFind(id);

        // Check the canceled status
        tenantQuery.checkTenantCanceled(tenantDb);
      }

      @Override
      protected Void process() {
        tenantDb.setStatus(enabled ? TenantStatus.ENABLED : TenantStatus.DISABLED);
        tenantRepo.save(tenantDb);

        // Save operation activity
        operationLogCmd.add(TENANT, tenantDb, enabled ? ENABLED : DISABLED);
        return null;
      }
    }.execute();
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void locked(Long id, Boolean locked, LocalDateTime lockStartDate,
      LocalDateTime lockEndDate) {
    new BizTemplate<Void>() {
      Tenant tenantDb;

      @Override
      protected void checkParams() {
        // Check operation permission when user action

        // Check date is valid
        assertTrue(!locked || isNull(lockStartDate)
                || isNull(lockEndDate) || lockEndDate.isAfter(lockStartDate),
            String.format("lockEndDate[%s] is not after lockStartDate[%s]",
                nonNull(lockEndDate) ? lockEndDate.format(DATE_TIME_FMT) : null,
                nonNull(lockStartDate) ? lockStartDate.format(DATE_TIME_FMT) : null));
        assertTrue(!locked || isNull(lockEndDate) // Ignore warning??
                || lockEndDate.isAfter(LocalDateTime.now()),
            String.format("lockEndDate[%s] must is a future date",
                nonNull(lockEndDate) ? lockEndDate.format(DATE_TIME_FMT) : null));

        // Check the tenant existed
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

        // Save operation activity
        operationLogCmd.add(TENANT, tenantDb, locked ? LOCKED : UNLOCKED);
        return null;
      }

    }.execute();
  }

  /**
   * User by {@link TenantLockedJob}
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
   * User by {@link TenantUnlockedJob}
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

  @Override
  public IdKey<Long, Object> add0(Tenant tenant) {
    return insert(tenant, "name");
  }

  @Override
  protected BaseRepository<Tenant, Long> getRepository() {
    return this.tenantRepo;
  }
}
