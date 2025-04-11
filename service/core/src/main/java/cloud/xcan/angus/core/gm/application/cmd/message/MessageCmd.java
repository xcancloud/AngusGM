package cloud.xcan.angus.core.gm.application.cmd.message;

import cloud.xcan.angus.core.gm.domain.message.Message;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.Set;


public interface MessageCmd {

  IdKey<Long, Object> add(Message message);

  void delete(Set<Long> ids);

  void plusReadNum(Set<Long> messageIds);

  void sentInSiteMessage(Message message);

  void sentEmailMessage(Message message);
}
