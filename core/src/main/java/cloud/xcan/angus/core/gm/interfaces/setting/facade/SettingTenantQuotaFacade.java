package cloud.xcan.angus.core.gm.interfaces.setting.facade;

import cloud.xcan.angus.api.gm.setting.dto.quota.QuotaCheckDto;
import cloud.xcan.angus.api.gm.setting.dto.quota.QuotaReplaceByOrderDto;
import cloud.xcan.angus.api.gm.setting.dto.quota.QuotaReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.dto.TenantQuotaFindDto;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.dto.TenantQuotaSearchDto;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.vo.tenant.TenantQuotaDetailVo;
import cloud.xcan.angus.remote.PageResult;
import java.util.HashSet;
import java.util.List;

public interface SettingTenantQuotaFacade {

  void quotaReplace(String name, Long quota);

  void quotaReplaceBatch(HashSet<QuotaReplaceDto> dto);

  void quotaReplaceByOrder(QuotaReplaceByOrderDto dto);

  Long newQuotaInit(String name);

  void quotaCheck(HashSet<QuotaCheckDto> dto);

  void quotaExpansionCheck(HashSet<QuotaCheckDto> dto);

  TenantQuotaDetailVo detail(String name);

  List<String> appList();

  PageResult<TenantQuotaDetailVo> list(TenantQuotaFindDto findDto);

  PageResult<TenantQuotaDetailVo> search(TenantQuotaSearchDto dto);

}
