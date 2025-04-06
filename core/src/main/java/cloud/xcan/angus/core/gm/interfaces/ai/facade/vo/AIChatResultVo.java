package cloud.xcan.angus.core.gm.interfaces.ai.facade.vo;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class AIChatResultVo {

  private String sessionId;
  private String messageId;
  private String messageType;
  private String contentType;
  private String eventStatus;
  private Map<String, Object> data;

}
