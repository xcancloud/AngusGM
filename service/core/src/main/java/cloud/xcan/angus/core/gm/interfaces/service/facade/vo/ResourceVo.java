package cloud.xcan.angus.core.gm.interfaces.service.facade.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class ResourceVo {

  private String resourceName;

  private String resourceDescription;

}
