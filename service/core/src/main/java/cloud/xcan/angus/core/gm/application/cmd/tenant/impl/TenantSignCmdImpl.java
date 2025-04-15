package cloud.xcan.angus.core.gm.application.cmd.tenant.impl;

import static cloud.xcan.angus.api.commonlink.UCConstant.TOP_TENANT_ADMIN;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertForbidden;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.gm.application.converter.TenantSignSmsConverter.toCancelConfirmSmsDto;
import static cloud.xcan.angus.core.gm.domain.UCCoreMessage.MOBILE_IS_UNBIND;
import static cloud.xcan.angus.core.gm.domain.operation.OperationResourceType.TENANT;
import static cloud.xcan.angus.core.gm.domain.operation.OperationType.TENANT_CANCEL;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.hasToRole;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isTenantSysAdmin;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.setMultiTenantCtrl;
import static cloud.xcan.angus.remote.message.http.Forbidden.M.FORBIDDEN_KEY;
import static cloud.xcan.angus.remote.message.http.Forbidden.M.NO_TENANT_SYS_ADMIN_PERMISSION;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.sms.SmsBizKey;
import cloud.xcan.angus.api.commonlink.tenant.Tenant;
import cloud.xcan.angus.api.commonlink.tenant.TenantRepo;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.commonlink.user.UserRepo;
import cloud.xcan.angus.api.enums.TenantStatus;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.operation.OperationLogCmd;
import cloud.xcan.angus.core.gm.application.cmd.sms.SmsCmd;
import cloud.xcan.angus.core.gm.application.cmd.tenant.TenantSignCmd;
import cloud.xcan.angus.core.gm.application.cmd.user.UserCmd;
import cloud.xcan.angus.core.gm.application.query.tenant.TenantQuery;
import cloud.xcan.angus.core.gm.infra.job.TenantSignCancelExpireJob;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

/**
 * Note: Allow the tenant system administrator to canceled the tenant. Allow the operation tenant
 * administrator to canceled the tenant.
 */
@Biz
@Slf4j
public class TenantSignCmdImpl extends CommCmd<Tenant, Long> implements TenantSignCmd {

  @Resource
  private UserRepo userRepo;

  @Resource
  private UserCmd userCmd;

  @Resource
  private SmsCmd smsCmd;

  @Resource
  private TenantRepo tenantRepo;

  @Resource
  private TenantQuery tenantQuery;

  @Resource
  private OperationLogCmd operationLogCmd;

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void cancelSignInvoke() {
    new BizTemplate<Void>() {
      Tenant tenantDb;
      final Long tenantId = getOptTenantId();

      @Override
      protected void checkParams() {
        // Check the tenant system administrator(Not op sysadmin) or TOP permission
        assertForbidden(isTenantSysAdmin() || hasToRole(TOP_TENANT_ADMIN),
            NO_TENANT_SYS_ADMIN_PERMISSION, FORBIDDEN_KEY);

        // Check the status is cancelling
        tenantDb = tenantQuery.checkAndFind(tenantId);
        assertTrue(tenantDb.getStatus().equals(TenantStatus.CANCELLING), "Account not cancelled");
      }

      @Override
      protected Void process() {
        Tenant tenantDb = tenantQuery.checkAndFind(tenantId);
        tenantDb.setStatus(TenantStatus.ENABLED);
        tenantDb.setApplyCancelDate(null);
        tenantRepo.save(tenantDb);
        return null;
      }
    }.execute();
  }

  /**
   * User by {@link TenantSignCancelExpireJob}
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void signCancelExpire(Set<Long> tenantIds) {
    new BizTemplate<Void>() {

      @Override
      protected Void process() {
        if (isNotEmpty(tenantIds)) {
          tenantRepo.updateStatusByTenantIdIn(tenantIds, TenantStatus.CANCELED.getValue());
          userCmd.deleteByTenant(tenantIds);
        }
        return null;
      }
    }.execute();
  }

  @Override
  public void signCancelSmsSend() {
    new BizTemplate<Void>() {
      User userDb;

      @Override
      protected void checkParams() {
        // Check the tenant system administrator(Not op sysadmin) or TOP permissions
        assertForbidden(isTenantSysAdmin()
            || hasToRole(TOP_TENANT_ADMIN), NO_TENANT_SYS_ADMIN_PERMISSION, FORBIDDEN_KEY);

        // Check the mobile is required
        setMultiTenantCtrl(false);
        userDb = userRepo.findByUserId(getUserId());
        assertForbidden(nonNull(userDb) && isNotEmpty(userDb.getMobile()), MOBILE_IS_UNBIND);
      }

      @Override
      protected Void process() {
        smsCmd.send(toCancelConfirmSmsDto(userDb.getMobile()), false);
        return null;
      }
    }.execute();
  }

  @Override
  public void signCancelSmsConfirm(String verificationCode) {
    new BizTemplate<Void>() {
      User userDb;
      Tenant tenantDb;

      @Override
      protected void checkParams() {
        // Check the tenant system administrator(Not op sysadmin) or TOP permissions
        assertForbidden(isTenantSysAdmin()
            || hasToRole(TOP_TENANT_ADMIN), NO_TENANT_SYS_ADMIN_PERMISSION, FORBIDDEN_KEY);

        // Check the mobile is required
        setMultiTenantCtrl(false);
        userDb = userRepo.findByUserId(getUserId());
        assertForbidden(isNotEmpty(userDb.getMobile()), MOBILE_IS_UNBIND);

        // Check the cancel sms is valid
        smsCmd.checkVerificationCode(SmsBizKey.SIGN_CANCEL, userDb.getMobile(), verificationCode);

        // Check and find tenant
        tenantDb = tenantQuery.checkAndFind(getOptTenantId());
      }

      @Override
      protected Void process() {
        tenantDb.setStatus(TenantStatus.CANCELLING);
        tenantDb.setApplyCancelDate(LocalDateTime.now());
        tenantRepo.save(tenantDb);

        // Save operation activity
        operationLogCmd.add(TENANT, tenantDb, TENANT_CANCEL);
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<Tenant, Long> getRepository() {
    return this.tenantRepo;
  }
}
