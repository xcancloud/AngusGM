package cloud.xcan.angus.core.gm.interfaces.ai.facade.internal;

import cloud.xcan.angus.core.gm.application.query.ai.AIChatQuery;
import cloud.xcan.angus.core.gm.interfaces.ai.facade.AIChatFacade;
import cloud.xcan.angus.core.gm.interfaces.ai.facade.dto.AIChatResultDto;
import jakarta.annotation.Resource;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class AIChatFacadeImpl implements AIChatFacade {

  @Resource
  private AIChatQuery aiChatQuery;

  @Override
  public Map<String, Object> chatResult(AIChatResultDto dto) {
    return aiChatQuery.chatResult(dto.getType(), dto.getQuestion());
  }
}
