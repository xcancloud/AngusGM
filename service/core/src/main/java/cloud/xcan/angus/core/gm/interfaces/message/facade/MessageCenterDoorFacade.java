package cloud.xcan.angus.core.gm.interfaces.message.facade;


import cloud.xcan.angus.api.gm.message.dto.MessageCenterPushDto;

public interface MessageCenterDoorFacade {

  void sendMessage(MessageCenterPushDto dto);

}
