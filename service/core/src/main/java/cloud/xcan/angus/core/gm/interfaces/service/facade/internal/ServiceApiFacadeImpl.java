package cloud.xcan.angus.core.gm.interfaces.service.facade.internal;

import cloud.xcan.angus.api.commonlink.api.Api;
import cloud.xcan.angus.core.gm.application.cmd.service.ServiceCmd;
import cloud.xcan.angus.core.gm.application.query.service.ServiceQuery;
import cloud.xcan.angus.core.gm.interfaces.service.facade.ServiceApiFacade;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.ServiceApiAddDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.internal.assembler.ServiceAssembler;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceApiVo;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;


@Component
public class ServiceApiFacadeImpl implements ServiceApiFacade {

  @Resource
  private ServiceCmd serviceCmd;

  @Resource
  private ServiceQuery serviceQuery;

  @Override
  public List<IdKey<Long, Object>> apiAdd(List<ServiceApiAddDto> dto) {
    List<Api> apis = dto.stream().map(ServiceAssembler::addApiDtoToDomain)
        .collect(Collectors.toList());
    return serviceCmd.apiAdd(apis);
  }

  @Override
  public void syncServiceApi(String serviceCode) {
    serviceCmd.syncServiceApi(serviceCode);
  }

  @Override
  public void discoveryApiSync() {
    serviceCmd.discoveryApiSync();
  }

  @Override
  public void apiDelete(Long id, HashSet<Long> ids) {
    serviceCmd.apiDelete(id, ids);
  }

  @Override
  public List<ServiceApiVo> apiList(Long id) {
    List<Api> apis = serviceQuery.apiList(id);
    return apis.stream().map(ServiceAssembler::toServiceApiVo).collect(Collectors.toList());
  }

}
