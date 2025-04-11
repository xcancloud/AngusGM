package cloud.xcan.angus.core.gm.application.query.setting.impl;

import static cloud.xcan.angus.core.gm.domain.CommonMessage.QUOTA_VALUE_ERROR_T2;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;

import cloud.xcan.angus.api.commonlink.setting.Setting;
import cloud.xcan.angus.api.commonlink.setting.SettingKey;
import cloud.xcan.angus.api.commonlink.setting.quota.Quota;
import cloud.xcan.angus.api.commonlink.setting.quota.QuotaResource;
import cloud.xcan.angus.api.commonlink.setting.tenant.quota.SettingTenantQuota;
import cloud.xcan.angus.api.commonlink.setting.tenant.quota.SettingTenantQuotaRepo;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.ProtocolAssert;
import cloud.xcan.angus.core.gm.application.query.setting.SettingQuery;
import cloud.xcan.angus.core.gm.application.query.setting.SettingTenantQuotaQuery;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import cloud.xcan.angus.spec.annotations.DoInFuture;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

@Biz
public class SettingTenantQuotaQueryImpl implements SettingTenantQuotaQuery {

  @Resource
  private SettingTenantQuotaRepo settingTenantQuotaRepo;

  @Resource
  private SettingQuery settingQuery;

  @Override
  public SettingTenantQuota detail(String name) {
    return new BizTemplate<SettingTenantQuota>() {

      @Override
      protected SettingTenantQuota process() {
        SettingTenantQuota tenantQuota = settingTenantQuotaRepo
            .findByTenantIdAndName(getOptTenantId(), name)
            .orElseThrow(() -> ResourceNotFound.of(name, "SettingTenantQuota"));

        // Associated tenant setting default value
        setDefaultQuota(tenantQuota, name);

        // Query and associate used
        // TODO
        return tenantQuota;
      }
    }.execute();
  }

  @Override
  public Page<SettingTenantQuota> find(Specification<SettingTenantQuota> spec,
      PageRequest pageable) {
    return new BizTemplate<Page<SettingTenantQuota>>(true, true) {

      @Override
      protected Page<SettingTenantQuota> process() {
        Page<SettingTenantQuota> tenantQuotas = settingTenantQuotaRepo.findAll(spec, pageable);
        if (tenantQuotas.hasContent()) {
          // Associated tenant setting default value
          setDefaultQuota(tenantQuotas);

          // Query and associate used
          // TODO
        }
        return tenantQuotas;
      }
    }.execute();
  }

  @DoInFuture("Optimize for loop calls to avoid performance problems")
  @Override
  public void quotaCheck(Map<QuotaResource, Long> quotasMap) {
    new BizTemplate<Void>() {

      @Override
      protected Void process() {
        for (QuotaResource quotaResource : quotasMap.keySet()) {
          // Check exists
          SettingTenantQuota tenantQuotaDb = checkAndFind(quotaResource.getValue());
          // Check value range
          ProtocolAssert.assertTrue(quotasMap.get(quotaResource) >= tenantQuotaDb.getMin()
                  && quotasMap.get(quotaResource) <= tenantQuotaDb.getMax(), QUOTA_VALUE_ERROR_T2,
              new Object[]{quotaResource, quotasMap.get(quotaResource), tenantQuotaDb.getMin(),
                  tenantQuotaDb.getMax()});
        }
        return null;
      }
    }.execute();
  }

  @DoInFuture("Optimize for loop calls to avoid performance problems")
  @Override
  public void quotaExpansionCheck(Map<QuotaResource, Long> quotasMap) {
    new BizTemplate<Void>() {

      @Override
      protected Void process() {
        for (QuotaResource quotaResource : quotasMap.keySet()) {
          // Check exists
          SettingTenantQuota tenantQuotaDb = checkAndFind(quotaResource.getValue());
          long newQuota = tenantQuotaDb.getQuota() + quotasMap.get(quotaResource);
          // Check value range
          ProtocolAssert.assertTrue(newQuota >= tenantQuotaDb.getMin()
                  && newQuota <= tenantQuotaDb.getMax(), QUOTA_VALUE_ERROR_T2,
              new Object[]{quotaResource, newQuota, tenantQuotaDb.getMin(),
                  tenantQuotaDb.getMax()});
        }
        return null;
      }
    }.execute();
  }


  @Override
  public List<String> appList() {
    return new BizTemplate<List<String>>() {


      @Override
      protected List<String> process() {
        return settingTenantQuotaRepo.findAppByTenantId(getOptTenantId());
      }
    }.execute();
  }

  @Override
  public SettingTenantQuota checkAndFind(String name) {
    return settingTenantQuotaRepo.findByTenantIdAndName(getOptTenantId(), name)
        .orElseThrow(() -> ResourceNotFound.of(name, "SettingTenantQuota"));
  }

  @Override
  public void setDefaultQuota(Page<SettingTenantQuota> tenantQuotas) {
    Setting defaultSetting = settingQuery.find0(SettingKey.QUOTA);
    Map<String, Quota> defaultQuotaMap = defaultSetting.toQuotaMap();
    for (SettingTenantQuota tenantQuota : tenantQuotas) {
      tenantQuota.setDefaults(defaultQuotaMap.get(tenantQuota.getName().getValue())
          .getQuota());
    }
  }

  public void setDefaultQuota(SettingTenantQuota tenantQuota, String name) {
    Setting defaultSetting = settingQuery.find0(SettingKey.QUOTA);
    Quota defaultQuota = defaultSetting.findQuotaByName(name);
    tenantQuota.setDefaults(defaultQuota.getQuota());
  }
}
