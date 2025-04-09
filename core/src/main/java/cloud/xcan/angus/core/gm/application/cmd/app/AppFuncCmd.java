package cloud.xcan.angus.core.gm.application.cmd.app;

import cloud.xcan.angus.api.commonlink.app.func.AppFunc;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;
import java.util.List;


public interface AppFuncCmd {

  List<IdKey<Long, Object>> add(Long appId, List<AppFunc> appFunc);

  void update(Long appId, List<AppFunc> appFunc);

  void replace(Long appId, List<AppFunc> appFunc);

  void delete(Long id, HashSet<Long> ids);

  void enabled(Long appId, List<AppFunc> appFunc);

  void imports(Long appId, List<AppFunc> appFunc);

}
