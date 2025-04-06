package cloud.xcan.angus.core.gm.interfaces.dept.facade.vo;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class DeptNavigationVo implements Serializable {

  private Long id;

  private String name;

  private String code;

  private Long pid;

  private Integer level;

}
