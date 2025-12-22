package cloud.xcan.angus.core.gm.application.query.service;

import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.*;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.*;

import java.util.List;

/**
 * <p>Service query service interface</p>
 */
public interface ServiceQuery {
    
    /**
     * <p>Get service detail</p>
     */
    ServiceDetailVo getDetail(String serviceName);
    
    /**
     * <p>List services</p>
     */
    List<ServiceListVo> list(ServiceFindDto dto);
    
    /**
     * <p>Get service statistics</p>
     */
    ServiceStatsVo getStats();
    
    /**
     * <p>Get service instance health</p>
     */
    ServiceHealthVo getInstanceHealth(String serviceName, String instanceId);
    
    /**
     * <p>Get Eureka config</p>
     */
    EurekaConfigVo getEurekaConfig();
    
    /**
     * <p>Get service call statistics</p>
     */
    ServiceCallStatsVo getServiceCallStats(String serviceName, ServiceCallStatsDto dto);
}
