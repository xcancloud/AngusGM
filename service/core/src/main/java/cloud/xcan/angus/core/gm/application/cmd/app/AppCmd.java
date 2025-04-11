package cloud.xcan.angus.core.gm.application.cmd.app;

import cloud.xcan.angus.core.gm.domain.app.App;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;
import java.util.List;

public interface AppCmd {

  IdKey<Long, Object> add(App app);

  void update(App app);

  IdKey<Long, Object> replace(App app);

  void siteUpdate(App app);

  void delete(HashSet<Long> ids);

  void enabled(List<App> app);

}
