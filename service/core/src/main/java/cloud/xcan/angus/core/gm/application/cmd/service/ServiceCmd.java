package cloud.xcan.angus.core.gm.application.cmd.service;

import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.*;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.*;

/**
 * <p>Service command service interface</p>
 */
public interface ServiceCmd {
    
    /**
     * <p>Refresh service list from Eureka</p>
     */
    ServiceRefreshVo refresh();
    
    /**
     * <p>Update service instance status</p>
     */
    ServiceInstanceStatusVo updateInstanceStatus(String serviceName, String instanceId, ServiceInstanceStatusDto dto);
    
    /**
     * <p>Update Eureka config</p>
     */
    EurekaConfigVo updateEurekaConfig(EurekaConfigUpdateDto dto);
    
    /**
     * <p>Test Eureka connection</p>
     */
    EurekaTestVo testEurekaConnection(EurekaTestDto dto);
}
