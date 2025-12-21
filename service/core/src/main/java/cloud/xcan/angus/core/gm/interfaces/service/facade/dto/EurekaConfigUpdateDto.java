package cloud.xcan.angus.core.gm.interfaces.service.facade.dto;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Eureka config update DTO
 */
@Getter
@Setter
@Accessors(chain = true)
public class EurekaConfigUpdateDto implements Serializable {

  private String serviceUrl;

  private Boolean enableAuth;

  private String username;

  private String password;

  private Integer syncInterval;

  private Boolean enableSsl;

  private Integer connectTimeout;

  private Integer readTimeout;
}
