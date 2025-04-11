package cloud.xcan.angus.api.commonlink.setting.aiagent;

import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class AIAgent implements Serializable {

  public static String DEFAULT_AI_AGENT_PROVIDER = "Tudou";

  private boolean enabled = false;

  @NotEmpty
  private String provider = DEFAULT_AI_AGENT_PROVIDER;

  private String agentId;

  private String chatIframe;

  private Map<String, Object> extensions = new HashMap<>();

}
