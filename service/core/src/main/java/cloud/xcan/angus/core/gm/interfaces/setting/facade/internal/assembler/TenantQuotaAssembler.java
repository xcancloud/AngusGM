package cloud.xcan.angus.core.gm.interfaces.setting.facade.internal.assembler;


import cloud.xcan.angus.api.commonlink.setting.tenant.quota.SettingTenantQuota;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.dto.TenantQuotaFindDto;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.dto.TenantQuotaSearchDto;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.vo.tenant.TenantQuotaDetailVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.core.utils.CoreUtils;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.data.jpa.domain.Specification;

public class TenantQuotaAssembler {

  public static TenantQuotaDetailVo toQuotaDetailVo(SettingTenantQuota quota) {
    return CoreUtils.copyProperties(quota, new TenantQuotaDetailVo());
  }

  public static Specification<SettingTenantQuota> getSpecification(TenantQuotaFindDto findDto) {
    // Build the final filters
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(findDto)
        .matchSearchFields("code", "name", "chineseName", "iso2", "iso3", "abbr")
        .rangeSearchFields("id")
        .orderByFields("id", "code")
        .build();
    return new GenericSpecification<>(filters);
  }

  public static Set<SearchCriteria> getSearchCriteria(TenantQuotaSearchDto dto) {
    // Build the final filters
    return new SearchCriteriaBuilder<>(dto)
        .matchSearchFields("name", "chineseName")
        .orderByFields("id", "createdDate")
        .build();
  }
}
