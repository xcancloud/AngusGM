package cloud.xcan.angus.core.gm.interfaces.app.facade.vo.org;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class AppUnauthUserVo {

  private Long id;

  private String fullName;

  private String avatar;

}
