package cloud.xcan.angus.core.gm.domain.dept;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class DeptSubCount implements Serializable {

  private long subDeptNum;

  private long sunUserNum;

}
