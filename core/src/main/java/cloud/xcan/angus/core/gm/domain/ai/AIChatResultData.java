package cloud.xcan.angus.core.gm.domain.ai;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class AIChatResultData {

  private String sessionId;
  private String messageId;
  private String messageType;
  private String contentType;
  private String eventStatus;
  private Map<String, Object> data;

}
