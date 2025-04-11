package cloud.xcan.angus.core.gm.domain.ai;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class AIChatMessageVo {

  private String session_id;
  private String message_id;
  private String message_type;
  private String content_type;
  private String event_status;
  private Map<String, Object> data;

}
