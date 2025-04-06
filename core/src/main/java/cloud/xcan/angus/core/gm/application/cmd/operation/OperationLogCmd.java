package cloud.xcan.angus.core.gm.application.cmd.operation;

import cloud.xcan.angus.core.gm.domain.operation.OperationLog;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.List;

public interface OperationLogCmd {

  List<IdKey<Long, Object>> add(List<OperationLog> optLog);

  void clearOperationLog(Integer clearBeforeDay);
}
