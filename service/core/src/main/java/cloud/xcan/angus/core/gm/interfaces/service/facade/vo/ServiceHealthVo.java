package cloud.xcan.angus.core.gm.interfaces.service.facade.vo;

import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Service instance health VO
 */
@Getter
@Setter
@Accessors(chain = true)
public class ServiceHealthVo implements Serializable {

  private String status;

  private Map<String, Object> details;
}
