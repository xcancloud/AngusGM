package cloud.xcan.angus.core.gm.application.cmd.operation;

import cloud.xcan.angus.core.gm.domain.operation.OperationLog;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.Collection;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public interface OperationLogCmd {

  void add(OperationLog operation);

  void addAll(List<OperationLog> operations);

  List<IdKey<Long, Object>> add(List<OperationLog> operations);

  void clearOperationLog(Integer clearBeforeDay);
}
