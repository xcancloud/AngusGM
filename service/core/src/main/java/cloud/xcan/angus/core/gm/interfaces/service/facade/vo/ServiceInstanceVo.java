package cloud.xcan.angus.core.gm.interfaces.service.facade.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Service instance VO
 */
@Getter
@Setter
@Accessors(chain = true)
public class ServiceInstanceVo implements Serializable {

  private String instanceId;

  private String hostName;

  private String ipAddr;

  private Integer port;

  private Integer securePort;

  private String status;

  private String healthCheckUrl;

  private String statusPageUrl;

  private String homePageUrl;

  private LocalDateTime lastHeartbeat;

  private String uptime;

  private Map<String, String> metadata;
}
