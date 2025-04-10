package cloud.xcan.angus.core.gm.application.cmd.operation;

import cloud.xcan.angus.api.commonlink.operation.OperationResource;
import cloud.xcan.angus.core.gm.domain.operation.OperationLog;
import cloud.xcan.angus.core.gm.domain.operation.OperationResourceType;
import cloud.xcan.angus.core.gm.domain.operation.OperationType;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.List;

public interface OperationLogCmd {

  void add(OperationResourceType resourceType, OperationResource<?> resource,
      OperationType operation, Object... params);

  void add(OperationLog operation);

  void addAll(OperationResourceType resourceType,
      List<? extends OperationResource<?>> resources, OperationType operation, Object... params);

  void addAll(List<OperationLog> operations);

  List<IdKey<Long, Object>> add(List<OperationLog> operations);

  void clearOperationLog(Integer clearBeforeDay);
}
