package cloud.xcan.angus.core.gm.interfaces.tenant.facade;

import cloud.xcan.angus.api.gm.tenant.dto.TenantAddByMobileDto;
import cloud.xcan.angus.api.gm.tenant.dto.TenantAddDto;
import cloud.xcan.angus.api.gm.tenant.dto.TenantFindDto;
import cloud.xcan.angus.api.gm.tenant.dto.TenantLockedDto;
import cloud.xcan.angus.api.gm.tenant.dto.TenantReplaceDto;
import cloud.xcan.angus.api.gm.tenant.dto.TenantSearchDto;
import cloud.xcan.angus.api.gm.tenant.dto.TenantUpdateDto;
import cloud.xcan.angus.api.gm.tenant.vo.TenantDetailVo;
import cloud.xcan.angus.api.gm.tenant.vo.TenantVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.remote.dto.EnabledOrDisabledDto;
import cloud.xcan.angus.spec.experimental.IdKey;


public interface TenantFacade {

  IdKey<Long, Object> add(TenantAddDto dto);

  IdKey<Long, Object> signupByMobile(TenantAddByMobileDto dto);

  void update(TenantUpdateDto dto);

  void replace(TenantReplaceDto dto);

  void enabled(EnabledOrDisabledDto dto);

  void locked(TenantLockedDto dto);

  TenantDetailVo detail(Long id);

  PageResult<TenantVo> list(TenantFindDto dto);

  PageResult<TenantVo> search(TenantSearchDto dto);


}
