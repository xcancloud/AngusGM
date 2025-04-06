package cloud.xcan.angus.core.gm.domain.ai;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class AIChatCreateSessionVo {

  private String code;

  private String msg;

  private AIChatCreateSessionDataVo data;

  private Map<String, Object> ext = new HashMap<>();

}
