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
import cloud.xcan.angus.core.biz.Biz;
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

@Slf4j
@Biz
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

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void quotaReplace(String name, Long quota) {
    new BizTemplate<Void>() {
      SettingTenantQuota tenantQuotaDb;

      @Override
      protected void checkParams() {
        // Check the system-wide quota name existed
        settingQuery.checkAndFindQuota(name);
        // Check the tenant-wide quota existed
        tenantQuotaDb = settingTenantQuotaQuery.checkAndFind(name);
        // Check the quota value range
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

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void quotaReplaceByOrder(Long orderId, Map<QuotaResource, Long> quotaMap, Long tenantId,
      String status, Boolean expired) {
    new BizTemplate<Void>() {

      @Override
      protected void checkParams() {
        // Check and find tenant order
        // NOOP
      }

      @Override
      protected Void process() {
        // Fix:: May be called by the /doorapi
        setOptTenantId(tenantId);
        if (/* Order status may not be submitted -> OrderCmd#payUpdate() */
            (isFinished() || isPending()) && expired) {
          for (QuotaResource resource : quotaMap.keySet()) {
            // Check the tenant quota existed
            SettingTenantQuota tenantQuotaDb = settingTenantQuotaQuery.checkAndFind(
                resource.getValue());
            long newQuota = tenantQuotaDb.getQuota() + quotaMap.get(resource);
            // Check the quota value range
            assertTrue(newQuota >= tenantQuotaDb.getMin()
                    && newQuota <= tenantQuotaDb.getMax(), QUOTA_VALUE_ERROR_T2,
                new Object[]{resource, newQuota, tenantQuotaDb.getMin(), tenantQuotaDb.getMax()});
            // Save new quota purchased
            // A value less than 0 appears, which may be repeatedly modified by the job during development and debugging
            tenantQuotaDb.setQuota(newQuota < 0 ? 0 : newQuota);
            settingTenantQuotaRepo.save(tenantQuotaDb);
          }
        } else if (expired) {
          for (QuotaResource resource : quotaMap.keySet()) {
            // Check the tenant quota existed
            SettingTenantQuota tenantQuotaDb = settingTenantQuotaQuery.checkAndFind(
                resource.getValue());
            long newQuota = tenantQuotaDb.getQuota() - quotaMap.get(resource);
            // Release expired quota
            // A value less than 0 appears, which may be repeatedly modified by the job during development and debugging
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
       * see AngusExpense OrderStatus enum
       */
      public boolean isPending() {
        return status.equals("CREATED") || status.equals("AWAITING_PAY");
      }

      /**
       * see AngusExpense OrderStatus enum
       */
      public boolean isFinished() {
        return status.equals("FINISHED");
      }
    }.execute();
  }


  /**
   * Initialize new quotas for all tenants.
   */
  @Override
  public Long newQuotaInit(String name) {
    return new BizTemplate<Long>(false) {
      Quota quotaData;

      @Override
      protected void checkParams() {
        // Check the system-wide quota name existed
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
            List<Long> tenantIds = settingTenantQuotaRepo.findInitializedTenantIds(
                idsPage.getContent(), name);
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
   * Adding new quotas requires manual initialization for tenants.
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
