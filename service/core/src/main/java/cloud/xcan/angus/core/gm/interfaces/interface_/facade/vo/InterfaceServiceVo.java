package cloud.xcan.angus.core.gm.interfaces.interface_.facade.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Interface service VO
 */
@Getter
@Setter
@Accessors(chain = true)
public class InterfaceServiceVo implements Serializable {

  private String serviceName;

  private String displayName;

  private String version;

  private String baseUrl;

  private LocalDateTime syncTime;

  private Integer interfaceCount;
}
