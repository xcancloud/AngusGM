package cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Interface tag VO
 */
@Getter
@Setter
@Accessors(chain = true)
public class InterfaceTagVo implements Serializable {

  private String name;

  private Integer interfaceCount;
}
