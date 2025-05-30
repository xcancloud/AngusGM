package cloud.xcan.angus.core.gm.application.cmd.message;

import cloud.xcan.angus.api.enums.ReceiveObjectType;
import java.util.List;

public interface MessageCenterOnlineCmd {

  void offline(ReceiveObjectType receiveObjectType, List<Long> receiveObjectIds);

  void updateOnlineStatus(String sessionId, String username, String userAgent, String deviceId,
      String remoteAddress, boolean online);

  void shutdown();

}
