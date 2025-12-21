package cloud.xcan.angus.core.gm.interfaces.service.facade.vo;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Eureka connection test response VO
 */
@Getter
@Setter
@Accessors(chain = true)
public class EurekaTestVo implements Serializable {

  private Boolean connected;

  private Integer responseTime;

  private Integer servicesCount;
}
