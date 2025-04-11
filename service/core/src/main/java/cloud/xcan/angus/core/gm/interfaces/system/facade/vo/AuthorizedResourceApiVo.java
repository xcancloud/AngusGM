package cloud.xcan.angus.core.gm.interfaces.system.facade.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class AuthorizedResourceApiVo {

  private Long id;

  private String name;

  private String code;

  private String description;

}
