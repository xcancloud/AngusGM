package cloud.xcan.angus.core.gm.interfaces.app.facade.vo.tag;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class AppTagInfoVo {

  private Long id;

  private String name;

}
