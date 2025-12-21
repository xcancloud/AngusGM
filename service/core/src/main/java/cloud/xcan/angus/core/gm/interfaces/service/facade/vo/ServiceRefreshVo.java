package cloud.xcan.angus.core.gm.interfaces.service.facade.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Service refresh response VO
 */
@Getter
@Setter
@Accessors(chain = true)
public class ServiceRefreshVo implements Serializable {

  private LocalDateTime refreshTime;

  private Integer totalServices;

  private Integer totalInstances;
}
