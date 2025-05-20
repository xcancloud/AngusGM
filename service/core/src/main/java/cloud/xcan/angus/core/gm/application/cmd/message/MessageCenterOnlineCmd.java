package cloud.xcan.angus.core.gm.application.cmd.message;

import cloud.xcan.angus.core.gm.infra.message.MessageCenterNoticeMessage;

public interface MessageCenterOnlineCmd {

  void offline(MessageCenterNoticeMessage message);

  void updateOnlineStatus(String username, String userAgent, String deviceId, String remoteAddress,
      Boolean online);

  void shutdown();

}
