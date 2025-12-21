package cloud.xcan.angus.core.gm.interfaces.monitoring.facade.vo;

import lombok.Data;

import java.util.Map;

@Data
public class HealthCheckVo {
    private String status;
    private Map<String, ComponentHealth> components;
    
    @Data
    public static class ComponentHealth {
        private String status;
        private Integer responseTime;
        private Map<String, Object> details;
    }
}
