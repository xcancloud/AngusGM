package cloud.xcan.angus.core.gm.application.cmd.to;

import cloud.xcan.angus.api.commonlink.to.TOUser;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.List;
import java.util.Set;

public interface TOUserCmd {

  List<IdKey<Long, Object>> add(List<TOUser> toUsers);

  void delete(Set<Long> userIds);

  void delete0(Set<Long> userIds);

}
