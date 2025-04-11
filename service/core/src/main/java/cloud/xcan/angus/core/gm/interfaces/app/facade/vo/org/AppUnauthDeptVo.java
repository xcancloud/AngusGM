package cloud.xcan.angus.core.gm.interfaces.app.facade.vo.org;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class AppUnauthDeptVo {

  private Long id;

  private String code;

  private String name;

  private Long pid;

  private Integer level;

}
