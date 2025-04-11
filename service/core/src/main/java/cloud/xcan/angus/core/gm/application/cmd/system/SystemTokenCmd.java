package cloud.xcan.angus.core.gm.application.cmd.system;

import cloud.xcan.angus.core.gm.domain.system.SystemToken;
import cloud.xcan.angus.core.gm.domain.system.resource.SystemTokenResource;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;


public interface SystemTokenCmd {

  SystemToken add(SystemToken systemToken, List<SystemTokenResource> resources);

  Void delete(HashSet<Long> ids);

  void deleteByApiIdIn(Collection<Long> ids);

}
