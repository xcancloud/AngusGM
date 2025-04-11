package cloud.xcan.angus.core.gm.application.cmd.message;

import java.util.Set;


public interface MessageCurrentCmd {

  void delete(Set<Long> ids);

  void read(Set<Long> ids);
}
