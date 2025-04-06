package cloud.xcan.angus.core.gm.interfaces.dept.facade.vo;

import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class DeptNavigationTreeVo implements Serializable {

  private DeptNavigationVo current;

  private List<DeptNavigationVo> parentChain;

}
