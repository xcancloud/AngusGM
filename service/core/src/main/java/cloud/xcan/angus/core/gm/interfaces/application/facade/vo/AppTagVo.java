package cloud.xcan.angus.core.gm.interfaces.application.facade.vo;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Application available tag VO
 */
@Getter
@Setter
@Accessors(chain = true)
public class AppTagVo implements Serializable {

  private Long id;

  private String name;

  private String description;
}
