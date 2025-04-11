package cloud.xcan.angus.core.gm.interfaces.ai.facade.internal.assembler;

import cloud.xcan.angus.core.gm.domain.ai.AIChatResultData;
import cloud.xcan.angus.core.gm.interfaces.ai.facade.vo.AIChatResultVo;

public class AIChatAssembler {

  public static AIChatResultVo toChatResultVo(AIChatResultData resultData) {
    return new AIChatResultVo().setSessionId(resultData.getSessionId())
        .setMessageId(resultData.getMessageId())
        .setMessageType(resultData.getMessageType())
        .setContentType(resultData.getContentType())
        .setEventStatus(resultData.getEventStatus())
        .setData(resultData.getData());
  }
}
