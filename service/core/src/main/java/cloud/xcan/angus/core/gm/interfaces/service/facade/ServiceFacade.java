package cloud.xcan.angus.core.gm.interfaces.service.facade;

import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.EurekaConfigUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.EurekaTestDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.ServiceCallStatsDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.ServiceFindDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.ServiceInstanceStatusDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.EurekaConfigVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.EurekaTestVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceCallStatsVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceDetailVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceHealthVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceInstanceStatusVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceListVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceRefreshVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceStatsVo;
import java.util.List;

/**
 * Service management facade interface
 */
public interface ServiceFacade {

  /**
   * Refresh service list from Eureka
   */
  ServiceRefreshVo refresh();

  /**
   * Update service instance status
   */
  ServiceInstanceStatusVo updateInstanceStatus(String serviceName, String instanceId, ServiceInstanceStatusDto dto);

  /**
   * Get service detail
   */
  ServiceDetailVo getDetail(String serviceName);

  /**
   * List services
   */
  List<ServiceListVo> list(ServiceFindDto dto);

  /**
   * Get service statistics
   */
  ServiceStatsVo getStats();

  /**
   * Get service instance health
   */
  ServiceHealthVo getInstanceHealth(String serviceName, String instanceId);

  /**
   * Get Eureka config
   */
  EurekaConfigVo getEurekaConfig();

  /**
   * Update Eureka config
   */
  EurekaConfigVo updateEurekaConfig(EurekaConfigUpdateDto dto);

  /**
   * Test Eureka connection
   */
  EurekaTestVo testEurekaConnection(EurekaTestDto dto);

  /**
   * Get service call statistics
   */
  ServiceCallStatsVo getServiceCallStats(String serviceName, ServiceCallStatsDto dto);
}
