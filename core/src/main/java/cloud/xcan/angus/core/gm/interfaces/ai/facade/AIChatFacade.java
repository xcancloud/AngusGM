package cloud.xcan.angus.core.gm.interfaces.ai.facade;

import cloud.xcan.angus.core.gm.interfaces.ai.facade.dto.AIChatResultDto;
import java.util.Map;

public interface AIChatFacade {

  Map<String, Object> chatResult(AIChatResultDto dto);

}
