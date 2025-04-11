package cloud.xcan.angus.core.gm.application.cmd.api;

import cloud.xcan.angus.core.gm.domain.api.log.ApiLog;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.List;


public interface ApiLogCmd {

  List<IdKey<Long, Object>> add(List<ApiLog> apiLogs);

  void clearOperationLog(Integer clearBeforeDay);
}
