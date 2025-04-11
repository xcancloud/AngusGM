package cloud.xcan.angus.core.gm.interfaces.setting.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.setting.facade.internal.assembler.TenantQuotaAssembler.getSearchCriteria;
import static cloud.xcan.angus.core.gm.interfaces.setting.facade.internal.assembler.TenantQuotaAssembler.getSpecification;
import static cloud.xcan.angus.core.gm.interfaces.setting.facade.internal.assembler.TenantQuotaAssembler.toQuotaDetailVo;
import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.api.commonlink.setting.quota.QuotaResource;
import cloud.xcan.angus.api.commonlink.setting.tenant.quota.SettingTenantQuota;
import cloud.xcan.angus.api.gm.setting.dto.quota.QuotaCheckDto;
import cloud.xcan.angus.api.gm.setting.dto.quota.QuotaReplaceByOrderDto;
import cloud.xcan.angus.api.gm.setting.dto.quota.QuotaReplaceDto;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.setting.SettingTenantQuotaCmd;
import cloud.xcan.angus.core.gm.application.query.setting.SettingTenantQuotaQuery;
import cloud.xcan.angus.core.gm.application.query.setting.SettingTenantQuotaSearch;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.SettingTenantQuotaFacade;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.dto.TenantQuotaFindDto;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.dto.TenantQuotaSearchDto;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.internal.assembler.TenantQuotaAssembler;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.vo.tenant.TenantQuotaDetailVo;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class SettingTenantQuotaFacadeImpl implements SettingTenantQuotaFacade {

  @Resource
  private SettingTenantQuotaQuery settingTenantQuotaQuery;

  @Resource
  private SettingTenantQuotaSearch settingTenantQuotaSearch;

  @Resource
  private SettingTenantQuotaCmd settingTenantQuotaCmd;

  @Override
  public void quotaReplace(String name, Long quota) {
    settingTenantQuotaCmd.quotaReplace(name, quota);
  }

  @Override
  public void quotaReplaceBatch(HashSet<QuotaReplaceDto> dto) {
    Map<QuotaResource, Long> dtoMap = dto.stream()
        .collect(Collectors.toMap(QuotaReplaceDto::getName, QuotaReplaceDto::getQuota));
    settingTenantQuotaCmd.quotaReplaceBatch(dtoMap);
  }

  @Override
  public void quotaReplaceByOrder(QuotaReplaceByOrderDto dto) {
    Map<QuotaResource, Long> quotaMap = dto.getQuotas().stream()
        .collect(Collectors.toMap(QuotaReplaceDto::getName, QuotaReplaceDto::getQuota));
    settingTenantQuotaCmd.quotaReplaceByOrder(dto.getOrderId(), quotaMap, dto.getTenantId(),
        dto.getStatus(), dto.getExpired());
  }

  @Override
  public Long newQuotaInit(String name) {
    return settingTenantQuotaCmd.newQuotaInit(name);
  }

  @Override
  public void quotaCheck(HashSet<QuotaCheckDto> dto) {
    Map<QuotaResource, Long> dtoMap = dto.stream()
        .collect(Collectors.toMap(QuotaCheckDto::getName, QuotaCheckDto::getQuota));
    settingTenantQuotaQuery.quotaCheck(dtoMap);
  }

  @Override
  public void quotaExpansionCheck(HashSet<QuotaCheckDto> dto) {
    Map<QuotaResource, Long> dtoMap = dto.stream()
        .collect(Collectors.toMap(QuotaCheckDto::getName, QuotaCheckDto::getQuota));
    settingTenantQuotaQuery.quotaExpansionCheck(dtoMap);
  }

  @NameJoin
  @Override
  public TenantQuotaDetailVo detail(String name) {
    SettingTenantQuota quota = settingTenantQuotaQuery.detail(name);
    return toQuotaDetailVo(quota);
  }

  @Override
  public List<String> appList() {
    return settingTenantQuotaQuery.appList();
  }

  @NameJoin
  @Override
  public PageResult<TenantQuotaDetailVo> list(TenantQuotaFindDto findDto) {
    Page<SettingTenantQuota> quotas = settingTenantQuotaQuery
        .find(getSpecification(findDto), findDto.tranPage());
    return buildVoPageResult(quotas, TenantQuotaAssembler::toQuotaDetailVo);
  }

  @NameJoin
  @Override
  public PageResult<TenantQuotaDetailVo> search(TenantQuotaSearchDto dto) {
    Page<SettingTenantQuota> page = settingTenantQuotaSearch
        .search(getSearchCriteria(dto), dto.tranPage(), SettingTenantQuota.class,
            getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, TenantQuotaAssembler::toQuotaDetailVo);
  }

}
