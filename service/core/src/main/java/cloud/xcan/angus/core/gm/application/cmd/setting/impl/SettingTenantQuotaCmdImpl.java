package cloud.xcan.angus.core.gm.application.cmd.setting.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.gm.domain.CommonMessage.QUOTA_VALUE_ERROR_T;
import static cloud.xcan.angus.core.gm.domain.CommonMessage.QUOTA_VALUE_ERROR_T2;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.setOptTenantId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;

import cloud.xcan.angus.api.commonlink.setting.SettingKey;
import cloud.xcan.angus.api.commonlink.setting.quota.Quota;
import cloud.xcan.angus.api.commonlink.setting.quota.QuotaResource;
import cloud.xcan.angus.api.commonlink.setting.tenant.quota.SettingTenantQuota;
import cloud.xcan.angus.api.commonlink.setting.tenant.quota.SettingTenantQuotaRepo;
import cloud.xcan.angus.api.commonlink.tenant.TenantRepo;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.setting.SettingTenantQuotaCmd;
import cloud.xcan.angus.core.gm.application.query.setting.SettingQuery;
import cloud.xcan.angus.core.gm.application.query.setting.SettingTenantQuotaQuery;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.utils.CoreUtils;
import cloud.xcan.angus.spec.annotations.DoInFuture;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * Implementation of tenant quota setting command operations.
 * </p>
 * <p>
 * Manages tenant-level quota settings including resource limits, activity quotas, and metrics
 * retention periods.
 * </p>
 * <p>
 * Provides quota initialization, updates, and batch operations with proper validation and range
 * checking.
 * </p>
 */
@Slf4j
@org.springframework.stereotype.Service
public class SettingTenantQuotaCmdImpl extends CommCmd<SettingTenantQuota, Long>
    implements SettingTenantQuotaCmd {

  @Resource
  private SettingTenantQuotaRepo settingTenantQuotaRepo;
  @Resource
  private SettingTenantQuotaQuery settingTenantQuotaQuery;
  @Resource
  private SettingQuery settingQuery;
  @Resource
  private TenantRepo tenantRepo;

  /**
   * <p>
   * Replaces tenant quota setting with validation.
   * </p>
   * <p>
   * Validates quota value against minimum and maximum limits before updating. Only updates if the
   * new quota value differs from the current value.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void quotaReplace(String name, Long quota) {
    new BizTemplate<Void>() {
      SettingTenantQuota tenantQuotaDb;

      @Override
      protected void checkParams() {
        // Verify system-wide quota name exists
        settingQuery.checkAndFindQuota(name);
        // Verify tenant-wide quota exists
        tenantQuotaDb = settingTenantQuotaQuery.checkAndFind(name);
        // Validate quota value range
        assertTrue(quota >= tenantQuotaDb.getMin() && quota <= tenantQuotaDb.getMax(),
            QUOTA_VALUE_ERROR_T, new Object[]{quota, tenantQuotaDb.getMin(),
                tenantQuotaDb.getMax()});
      }

      @Override
      protected Void process() {
        if (!quota.equals(tenantQuotaDb.getQuota())) {
          tenantQuotaDb.setQuota(quota);
          settingTenantQuotaRepo.save(tenantQuotaDb);
        }
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Replaces multiple quota settings in batch.
   * </p>
   * <p>
   * Processes multiple quota updates in a single operation. Note: Future optimization needed to
   * avoid performance issues with loop calls.
   * </p>
   */
  @DoInFuture("Optimize for loop calls to avoid performance problems")
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void quotaReplaceBatch(Map<QuotaResource, Long> quotasMap) {
    new BizTemplate<Void>() {

      @Override
      protected Void process() {
        for (QuotaResource quotaResource : quotasMap.keySet()) {
          quotaReplace(quotaResource.getValue(), quotasMap.get(quotaResource));
        }
        return null;
      }
    }.execute();
  }

  /**
   * <p>
   * Replaces tenant quotas based on order status and expiration.
   * </p>
   * <p>
   * Updates quotas based on order completion status and expiration flags. Handles quota addition
   * for completed orders and quota release for expired orders.
   * </p>
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void quotaReplaceByOrder(Long orderId, Map<QuotaResource, Long> quotaMap, Long tenantId,
      String status, Boolean expired) {
    new BizTemplate<Void>() {

      @Override
      protected void checkParams() {
        // Verify tenant order exists
        // NOOP - validation handled in business logic
      }

      @Override
      protected Void process() {
        // Set tenant context for inner API calls
        setOptTenantId(tenantId);
        if (/* Order status may not be submitted -> OrderCmd#payUpdate() */
            (isFinished() || isPending()) && expired) {
          // Add quotas for completed orders
          for (QuotaResource resource : quotaMap.keySet()) {
            // Verify tenant quota exists
            SettingTenantQuota tenantQuotaDb = settingTenantQuotaQuery.checkAndFind(
                resource.getValue());
            long newQuota = tenantQuotaDb.getQuota() + quotaMap.get(resource);
            // Validate quota value range
            assertTrue(newQuota >= tenantQuotaDb.getMin()
                    && newQuota <= tenantQuotaDb.getMax(), QUOTA_VALUE_ERROR_T2,
                new Object[]{resource, newQuota, tenantQuotaDb.getMin(), tenantQuotaDb.getMax()});
            // Save new quota purchased
            // Handle negative values that may occur during development and debugging
            tenantQuotaDb.setQuota(newQuota < 0 ? 0 : newQuota);
            settingTenantQuotaRepo.save(tenantQuotaDb);
          }
        } else if (expired) {
          // Release quotas for expired orders
          for (QuotaResource resource : quotaMap.keySet()) {
            // Verify tenant quota exists
            SettingTenantQuota tenantQuotaDb = settingTenantQuotaQuery.checkAndFind(
                resource.getValue());
            long newQuota = tenantQuotaDb.getQuota() - quotaMap.get(resource);
            // Release expired quota
            // Handle negative values that may occur during development and debugging
            tenantQuotaDb.setQuota(newQuota < 0 ? 0 : newQuota);
            settingTenantQuotaRepo.save(tenantQuotaDb);
          }
        } else {
          assertTrue(true, String
              .format("The order status[%s] is illegal when changing the quota, order expired: %s",
                  status, expired));
        }
        return null;
      }

      /**
       * <p>
       * Checks if order status is pending.
       * </p>
       * <p>
       * See AngusExpense OrderStatus enum for status values.
       * </p>
       */
      public boolean isPending() {
        return status.equals("CREATED") || status.equals("AWAITING_PAY");
      }

      /**
       * <p>
       * Checks if order status is finished.
       * </p>
       * <p>
       * See AngusExpense OrderStatus enum for status values.
       * </p>
       */
      public boolean isFinished() {
        return status.equals("FINISHED");
      }
    }.execute();
  }

  /**
   * <p>
   * Initializes new quotas for all tenants.
   * </p>
   * <p>
   * Creates quota settings for all existing tenants that don't have the specified quota type
   * initialized.
   * </p>
   */
  @Override
  public Long newQuotaInit(String name) {
    return new BizTemplate<Long>(false) {
      Quota quotaData;

      @Override
      protected void checkParams() {
        // Verify system-wide quota name exists
        quotaData = settingQuery.checkAndFindQuota(name);
      }

      @Override
      protected Long process() {
        long initNum = 0;
        int page = 0, size = 200;
        Page<Long> idsPage;
        do {
          idsPage = tenantRepo.findAllIds(PageRequest.of(page, size));
          if (idsPage.hasContent()) {
            // Find tenants that already have this quota initialized
            List<Long> tenantIds = settingTenantQuotaRepo.findInitializedTenantIds(
                idsPage.getContent(), name);
            // Calculate tenants that need initialization
            List<Long> unInitTenantIds = new ArrayList<>(idsPage.getContent());
            unInitTenantIds.removeAll(tenantIds);
            add0(quotaData, unInitTenantIds);
            initNum += unInitTenantIds.size();
          }
          page++;
        } while (idsPage.hasNext());
        return initNum;
      }
    }.execute();
  }

  /**
   * <p>
   * Adds quota settings for multiple tenants.
   * </p>
   * <p>
   * Creates quota settings for the specified tenants using the provided quota data.
   * </p>
   */
  @Transactional
  @Override
  public void add0(Quota quotaData, List<Long> tenantIds) {
    if (isNull(quotaData) || isEmpty(tenantIds)) {
      return;
    }
    List<SettingTenantQuota> tenantQuotas = tenantIds.stream()
        .map(x -> {
          SettingTenantQuota tenantQuota = new SettingTenantQuota();
          CoreUtils.copyProperties(quotaData, tenantQuota);
          tenantQuota.setTenantId(x);
          return tenantQuota;
        }).collect(Collectors.toList());
    batchInsert0(tenantQuotas);
  }

  /**
   * <p>
   * Initializes quota settings for a new tenant.
   * </p>
   * <p>
   * Creates quota settings for all quota types when a new tenant is created. Manual initialization
   * is required when new quota types are added.
   * </p>
   */
  @Override
  public void init(Long tenantId) {
    if (!settingTenantQuotaRepo.existsByTenantId(tenantId)) {
      List<SettingTenantQuota> tenantQuotas = settingQuery.find0(SettingKey.QUOTA).getQuota()
          .stream().map(x -> {
            SettingTenantQuota tenantQuota = new SettingTenantQuota();
            CoreUtils.copyProperties(x, tenantQuota);
            tenantQuota.setTenantId(tenantId);
            return tenantQuota;
          }).collect(Collectors.toList());
      batchInsert0(tenantQuotas);
    }
  }

  @Override
  protected BaseRepository<SettingTenantQuota, Long> getRepository() {
    return settingTenantQuotaRepo;
  }
}
