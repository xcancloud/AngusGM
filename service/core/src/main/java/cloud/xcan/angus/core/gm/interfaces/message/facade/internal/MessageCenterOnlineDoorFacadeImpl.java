package cloud.xcan.angus.core.gm.interfaces.message.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.message.facade.internal.assembler.MessageCenterOnlineAssembler.dtoToOfflineDomain;

import cloud.xcan.angus.core.gm.application.cmd.message.MessageCenterCmd;
import cloud.xcan.angus.core.gm.interfaces.message.facade.MessageCenterOnlineDoorFacade;
import cloud.xcan.angus.core.gm.interfaces.message.facade.dto.MessageCenterOfflineDto;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class MessageCenterOnlineDoorFacadeImpl implements MessageCenterOnlineDoorFacade {

  @Resource
  private MessageCenterCmd messageCenterCmd;

  @Override
  public void offline(MessageCenterOfflineDto dto) {
    messageCenterCmd.push(dtoToOfflineDomain(dto));
  }

}
