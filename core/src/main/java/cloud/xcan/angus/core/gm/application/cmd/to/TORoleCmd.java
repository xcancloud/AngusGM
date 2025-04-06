package cloud.xcan.angus.core.gm.application.cmd.to;

import cloud.xcan.angus.api.commonlink.to.TORole;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;
import java.util.List;


public interface TORoleCmd {

  List<IdKey<Long, Object>> add(List<TORole> roles);

  void update(List<TORole> roles);

  List<IdKey<Long, Object>> replace(List<TORole> roles);

  void delete(HashSet<Long> ids);

  void enabled(List<TORole> roles);

}
