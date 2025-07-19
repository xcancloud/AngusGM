package cloud.xcan.angus.core.gm.interfaces.setting.facade.internal.assembler;


import cloud.xcan.angus.api.commonlink.setting.tenant.quota.SettingTenantQuota;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.dto.TenantQuotaFindDto;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.vo.tenant.TenantQuotaDetailVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;

public class TenantQuotaAssembler {

  public static TenantQuotaDetailVo toQuotaDetailVo(SettingTenantQuota quota) {
    return new TenantQuotaDetailVo().setId(quota.getId())
        .setName(quota.getName())
        .setAppCode(quota.getAppCode())
        .setServiceCode(quota.getServiceCode())
        .setAllowChange(quota.getAllowChange())
        .setCalcRemaining(quota.getCalcRemaining())
        .setQuota(quota.getQuota())
        .setMin(quota.getMin())
        .setMax(quota.getMax())
        .setDefault0(quota.getDefault0())
        .setTenantId(quota.getTenantId());
  }

  public static GenericSpecification<SettingTenantQuota> getSpecification(TenantQuotaFindDto dto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .matchSearchFields("name")
        .rangeSearchFields("id", "createdDate")
        .orderByFields("id", "code", "createdDate")
        .build();
    return new GenericSpecification<>(filters);
  }

}
