package cloud.xcan.angus.core.gm.interfaces.user.facade.vo.current;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
public class CheckSecretVo implements Serializable {

  private String linkSecret;

}
