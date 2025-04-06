package cloud.xcan.angus.core.gm.interfaces.operation.facade;


import cloud.xcan.angus.core.event.source.UserOperation;
import cloud.xcan.angus.core.gm.interfaces.operation.facade.dto.OperationFindDto;
import cloud.xcan.angus.core.gm.interfaces.operation.facade.vo.OperationLogVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.List;


public interface OperationFacade {

  PageResult<OperationLogVo> list(OperationFindDto dto);

  List<IdKey<Long, Object>> add(List<UserOperation> dto);
}
