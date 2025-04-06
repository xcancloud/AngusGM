package cloud.xcan.angus.core.gm.application.cmd.message;

import cloud.xcan.angus.core.gm.infra.message.MessageCenterNoticeMessage;

public interface MessageCenterCmd {

  void send(MessageCenterNoticeMessage message);

  void sendNoticeMessage(MessageCenterNoticeMessage message);

}
