package cloud.xcan.angus.core.gm.application.cmd.email;

import cloud.xcan.angus.core.gm.domain.email.server.EmailServer;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;

public interface EmailServerCmd {

  IdKey<Long, Object> add(EmailServer emailServer);

  void update(EmailServer emailServer);

  IdKey<Long, Object> replace(EmailServer emailServer);

  void delete(HashSet<Long> ids);

  void enabled(Long id, Boolean enabled);
}
