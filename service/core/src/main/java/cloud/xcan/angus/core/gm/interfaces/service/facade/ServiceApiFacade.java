package cloud.xcan.angus.core.gm.interfaces.service.facade;

import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.ServiceApiAddDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceApiVo;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;
import java.util.List;

public interface ServiceApiFacade {

  List<IdKey<Long, Object>> apiAdd(List<ServiceApiAddDto> dto);

  void syncServiceApi(String serviceCode);

  void discoveryApiSync();

  void apiDelete(Long id, HashSet<Long> ids);

  List<ServiceApiVo> apiList(Long id);

}
