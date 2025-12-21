package cloud.xcan.angus.core.gm.interfaces.tenant.facade;

import cloud.xcan.angus.core.gm.interfaces.tenant.facade.dto.TenantConfigUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.dto.TenantCreateDto;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.dto.TenantFindDto;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.dto.TenantStatusUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.dto.TenantUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.vo.TenantConfigVo;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.vo.TenantDetailVo;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.vo.TenantListVo;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.vo.TenantStatsVo;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.vo.TenantStatusUpdateVo;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.vo.TenantUsageVo;
import cloud.xcan.angus.remote.PageResult;

public interface TenantFacade {

  TenantDetailVo create(TenantCreateDto dto);

  TenantDetailVo update(Long id, TenantUpdateDto dto);

  TenantStatusUpdateVo updateStatus(Long id, TenantStatusUpdateDto dto);

  void delete(Long id);

  TenantDetailVo getDetail(Long id);

  PageResult<TenantListVo> list(TenantFindDto dto);

  TenantStatsVo getStats();

  TenantConfigVo getConfig(Long id);

  TenantConfigVo updateConfig(Long id, TenantConfigUpdateDto dto);

  TenantUsageVo getUsage(Long id);
}
