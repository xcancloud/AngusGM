package cloud.xcan.angus.core.gm.interfaces.service.facade;

import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.ServiceAddDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.ServiceFindDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.ServiceReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.ServiceUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ResourceApiVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceResourceVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.remote.dto.EnabledOrDisabledDto;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;
import java.util.List;

public interface ServiceFacade {

  IdKey<Long, Object> add(ServiceAddDto dto);

  void update(ServiceUpdateDto dto);

  IdKey<Long, Object> replace(ServiceReplaceDto dto);

  void delete(HashSet<Long> ids);

  void enabled(List<EnabledOrDisabledDto> dto);

  ServiceVo detail(Long id);

  PageResult<ServiceVo> list(ServiceFindDto dto);

  List<ServiceResourceVo> resourceList(String serviceCode, Boolean auth);

  List<ResourceApiVo> resourceApiList(String serviceCode, String resourceName, Boolean auth);

}
