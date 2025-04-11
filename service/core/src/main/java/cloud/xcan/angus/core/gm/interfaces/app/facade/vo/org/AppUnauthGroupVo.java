package cloud.xcan.angus.core.gm.interfaces.app.facade.vo.org;

import cloud.xcan.angus.api.commonlink.group.GroupSource;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class AppUnauthGroupVo {

  private Long id;

  private String name;

  private String code;

  private Boolean enabled;

  private GroupSource source;

}
