package cloud.xcan.angus.core.gm.application.cmd.message;

import cloud.xcan.angus.core.gm.infra.message.MessageCenterNoticeMessage;

public interface MessageCenterCmd {

  void push(MessageCenterNoticeMessage message);

  void sendNoticeMessage(MessageCenterNoticeMessage message);

}
