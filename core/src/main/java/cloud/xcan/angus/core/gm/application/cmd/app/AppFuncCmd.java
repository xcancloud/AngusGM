package cloud.xcan.angus.core.gm.application.cmd.app;

import cloud.xcan.angus.api.commonlink.app.func.AppFunc;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;
import java.util.List;


public interface AppFuncCmd {

  List<IdKey<Long, Object>> add(Long appId, List<AppFunc> appFunctions);

  void update(Long appId, List<AppFunc> appFunctions);

  void replace(Long appId, List<AppFunc> appFunctions);

  void delete(Long id, HashSet<Long> ids);

  void enabled(Long appId, List<AppFunc> appFunctions);

  void importFuncs(Long appId, List<AppFunc> appFunctions);

}
