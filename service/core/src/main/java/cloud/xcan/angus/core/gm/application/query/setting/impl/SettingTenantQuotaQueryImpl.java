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
import cloud.xcan.angus.core.gm.domain.setting.SettingTenantQuotaRepoSearch;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import cloud.xcan.angus.spec.annotations.DoInFuture;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * <p>
 * Implementation of tenant quota setting query operations.
 * </p>
 * <p>
 * Manages tenant quota setting retrieval, validation, and quota management.
 * Provides comprehensive tenant quota querying with quota validation support.
 * </p>
 * <p>
 * Supports tenant quota detail retrieval, paginated listing, quota validation,
 * quota expansion checking, and default quota association for comprehensive quota administration.
 * </p>
 */
@Biz
public class SettingTenantQuotaQueryImpl implements SettingTenantQuotaQuery {

  @Resource
  private SettingTenantQuotaRepo settingTenantQuotaRepo;
  @Resource
  private SettingTenantQuotaRepoSearch settingTenantQuotaRepoSearch;
  @Resource
  private SettingQuery settingQuery;

  /**
   * <p>
   * Retrieves detailed tenant quota setting by name.
   * </p>
   * <p>
   * Fetches complete tenant quota record with default quota association.
   * Validates quota existence and associates default values.
   * </p>
   */
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

  /**
   * <p>
   * Retrieves tenant quota settings with optional filtering and search capabilities.
   * </p>
   * <p>
   * Supports full-text search and specification-based filtering.
   * Enriches results with default quota information for comprehensive display.
   * </p>
   */
  @Override
  public Page<SettingTenantQuota> list(GenericSpecification<SettingTenantQuota> spec,
      PageRequest pageable, boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<SettingTenantQuota>>(true, true) {

      @Override
      protected Page<SettingTenantQuota> process() {
        Page<SettingTenantQuota> tenantQuotas = fullTextSearch
            ? settingTenantQuotaRepoSearch.find(spec.getCriteria(), pageable,
            SettingTenantQuota.class, match)
            : settingTenantQuotaRepo.findAll(spec, pageable);
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

  /**
   * <p>
   * Validates quota values against tenant quota settings.
   * </p>
   * <p>
   * Checks if quota values are within allowed range for each quota resource.
   * Throws ProtocolException if quota values exceed limits.
   * </p>
   */
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

  /**
   * <p>
   * Validates quota expansion values against tenant quota settings.
   * </p>
   * <p>
   * Checks if expanded quota values are within allowed range for each quota resource.
   * Throws ProtocolException if expanded quota values exceed limits.
   * </p>
   */
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

  /**
   * <p>
   * Retrieves application list for current tenant.
   * </p>
   * <p>
   * Returns list of application names associated with current tenant.
   * Used for quota management and application filtering.
   * </p>
   */
  @Override
  public List<String> appList() {
    return new BizTemplate<List<String>>() {

      @Override
      protected List<String> process() {
        return settingTenantQuotaRepo.findAppByTenantId(getOptTenantId());
      }
    }.execute();
  }

  /**
   * <p>
   * Validates and retrieves tenant quota setting by name.
   * </p>
   * <p>
   * Returns tenant quota setting with existence validation.
   * Throws ResourceNotFound if tenant quota setting does not exist.
   * </p>
   */
  @Override
  public SettingTenantQuota checkAndFind(String name) {
    return settingTenantQuotaRepo.findByTenantIdAndName(getOptTenantId(), name)
        .orElseThrow(() -> ResourceNotFound.of(name, "SettingTenantQuota"));
  }

  /**
   * <p>
   * Sets default quota values for tenant quota page.
   * </p>
   * <p>
   * Loads default quota settings and associates with tenant quota records.
   * </p>
   */
  @Override
  public void setDefaultQuota(Page<SettingTenantQuota> tenantQuotas) {
    Setting defaultSetting = settingQuery.find0(SettingKey.QUOTA);
    Map<String, Quota> defaultQuotaMap = defaultSetting.toQuotaMap();
    for (SettingTenantQuota tenantQuota : tenantQuotas) {
      tenantQuota.setDefault0(defaultQuotaMap.get(tenantQuota.getName().getValue())
          .getQuota());
    }
  }

  /**
   * <p>
   * Sets default quota value for single tenant quota.
   * </p>
   * <p>
   * Loads default quota setting and associates with tenant quota record.
   * </p>
   */
  public void setDefaultQuota(SettingTenantQuota tenantQuota, String name) {
    Setting defaultSetting = settingQuery.find0(SettingKey.QUOTA);
    Quota defaultQuota = defaultSetting.findQuotaByName(name);
    tenantQuota.setDefault0(defaultQuota.getQuota());
  }
}
