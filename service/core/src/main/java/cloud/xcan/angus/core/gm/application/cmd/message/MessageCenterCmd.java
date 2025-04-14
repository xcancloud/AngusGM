package cloud.xcan.angus.core.gm.application.cmd.message;

import cloud.xcan.angus.api.gm.message.dto.MessageCenterPushDto;
import cloud.xcan.angus.core.gm.interfaces.message.facade.dto.MessageCenterOfflineDto;

public interface MessageCenterCmd {

  void push(MessageCenterPushDto message);

  void offline(MessageCenterOfflineDto message);

}
