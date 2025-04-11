package cloud.xcan.angus.core.gm.application.query.ai;

import cloud.xcan.angus.core.gm.infra.ai.AIChatType;
import java.util.Map;

public interface AIChatQuery {

  Map<String, Object> chatResult(AIChatType type, String question);

}
