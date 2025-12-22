package cloud.xcan.angus.core.gm.interfaces.service.facade.internal;

import cloud.xcan.angus.core.gm.application.cmd.service.ServiceCmd;
import cloud.xcan.angus.core.gm.application.query.service.ServiceQuery;
import cloud.xcan.angus.core.gm.interfaces.service.facade.ServiceFacade;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.*;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>Service management facade implementation</p>
 */
@Service
public class ServiceFacadeImpl implements ServiceFacade {

  @Resource
  private ServiceCmd serviceCmd;

  @Resource
  private ServiceQuery serviceQuery;

  @Override
  public ServiceRefreshVo refresh() {
    return serviceCmd.refresh();
  }

  @Override
  public ServiceInstanceStatusVo updateInstanceStatus(String serviceName, String instanceId, ServiceInstanceStatusDto dto) {
    return serviceCmd.updateInstanceStatus(serviceName, instanceId, dto);
  }

  @Override
  public ServiceDetailVo getDetail(String serviceName) {
    return serviceQuery.getDetail(serviceName);
  }

  @Override
  public List<ServiceListVo> list(ServiceFindDto dto) {
    return serviceQuery.list(dto);
  }

  @Override
  public ServiceStatsVo getStats() {
    return serviceQuery.getStats();
  }

  @Override
  public ServiceHealthVo getInstanceHealth(String serviceName, String instanceId) {
    return serviceQuery.getInstanceHealth(serviceName, instanceId);
  }

  @Override
  public EurekaConfigVo getEurekaConfig() {
    return serviceQuery.getEurekaConfig();
  }

  @Override
  public EurekaConfigVo updateEurekaConfig(EurekaConfigUpdateDto dto) {
    return serviceCmd.updateEurekaConfig(dto);
  }

  @Override
  public EurekaTestVo testEurekaConnection(EurekaTestDto dto) {
    return serviceCmd.testEurekaConnection(dto);
  }

  @Override
  public ServiceCallStatsVo getServiceCallStats(String serviceName, ServiceCallStatsDto dto) {
    return serviceQuery.getServiceCallStats(serviceName, dto);
  }
}
