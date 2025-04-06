package cloud.xcan.angus.core.gm.interfaces.message.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.message.facade.internal.assembler.MessageCenterAssembler.dtoToNoticeDomain;

import cloud.xcan.angus.api.gm.message.dto.MessageCenterPushDto;
import cloud.xcan.angus.core.gm.application.cmd.message.MessageCenterCmd;
import cloud.xcan.angus.core.gm.interfaces.message.facade.MessageCenterFacade;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class MessageCenterFacadeImpl implements MessageCenterFacade {

  @Resource
  private MessageCenterCmd messageCenterCmd;

  @Override
  public void send(MessageCenterPushDto dto) {
    messageCenterCmd.send(dtoToNoticeDomain(dto));
  }

}
