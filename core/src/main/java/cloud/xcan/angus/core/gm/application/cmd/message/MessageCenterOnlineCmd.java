package cloud.xcan.angus.core.gm.application.cmd.message;

import cloud.xcan.angus.core.gm.infra.message.MessageCenterNoticeMessage;
import org.springframework.transaction.annotation.Transactional;

public interface MessageCenterOnlineCmd {

  void offline(MessageCenterNoticeMessage message);

  void updateOnlineStatus(String username, Boolean online);

  @Transactional(rollbackFor = Exception.class)
  void updateOnlineStatus(Long userId, Boolean online);

  void shutdown();
}
