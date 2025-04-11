package cloud.xcan.angus.core.gm.interfaces.message.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.message.facade.internal.assembler.MessageCenterAssembler.dtoToNoticeDomain;

import cloud.xcan.angus.api.gm.message.dto.MessageCenterPushDto;
import cloud.xcan.angus.core.gm.application.cmd.message.MessageCenterCmd;
import cloud.xcan.angus.core.gm.interfaces.message.facade.MessageCenterDoorFacade;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class MessageCenterDoorFacadeImpl implements MessageCenterDoorFacade {

  @Resource
  private MessageCenterCmd messageCenterCmd;

  @Override
  public void sendMessage(MessageCenterPushDto dto) {
    messageCenterCmd.send(dtoToNoticeDomain(dto));
  }

}
