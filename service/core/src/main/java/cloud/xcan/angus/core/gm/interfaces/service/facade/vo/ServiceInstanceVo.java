package cloud.xcan.angus.core.gm.interfaces.service.facade.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * <p>Service instance VO</p>
 */
@Data
public class ServiceInstanceVo {

    /**
     * <p>Instance ID</p>
     */
    private String instanceId;

    /**
     * <p>Host name</p>
     */
    private String hostName;

    /**
     * <p>IP address</p>
     */
    private String ipAddr;

    /**
     * <p>Port</p>
     */
    private Integer port;

    /**
     * <p>Secure port</p>
     */
    private Integer securePort;

    /**
     * <p>Status (UP, DOWN, OUT_OF_SERVICE)</p>
     */
    private String status;

    /**
     * <p>Health check URL</p>
     */
    private String healthCheckUrl;

    /**
     * <p>Status page URL</p>
     */
    private String statusPageUrl;

    /**
     * <p>Home page URL</p>
     */
    private String homePageUrl;

    /**
     * <p>Last heartbeat time</p>
     */
    private LocalDateTime lastHeartbeat;

    /**
     * <p>Uptime</p>
     */
    private String uptime;

    /**
     * <p>Metadata</p>
     */
    private Map<String, String> metadata;
}
