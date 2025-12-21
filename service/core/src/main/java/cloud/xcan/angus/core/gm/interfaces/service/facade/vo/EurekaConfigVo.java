package cloud.xcan.angus.core.gm.interfaces.service.facade.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Eureka config VO
 */
@Getter
@Setter
@Accessors(chain = true)
public class EurekaConfigVo implements Serializable {

  private String serviceUrl;

  private Boolean enableAuth;

  private String username;

  private String password;

  private Integer syncInterval;

  private Boolean enableSsl;

  private Integer connectTimeout;

  private Integer readTimeout;

  private LocalDateTime modifiedDate;
}
