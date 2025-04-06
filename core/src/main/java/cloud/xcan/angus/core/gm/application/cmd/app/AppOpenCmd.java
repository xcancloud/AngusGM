package cloud.xcan.angus.core.gm.application.cmd.app;

import cloud.xcan.angus.api.commonlink.app.open.AppOpen;
import cloud.xcan.angus.core.gm.domain.app.App;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.List;

public interface AppOpenCmd {

  IdKey<Long, Object> open(AppOpen appOpen);

  void renew(AppOpen appOpen);

  void cancel(Long appId);

  void expiredUpdate();

  void open0(AppOpen appOpen, App appDb);

  void open0(List<AppOpen> appOpens, App appDb);

}
