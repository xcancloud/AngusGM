package cloud.xcan.angus.core.gm.interfaces.operation.facade;


import cloud.xcan.angus.core.event.source.UserOperation;
import cloud.xcan.angus.core.gm.interfaces.operation.facade.dto.OperationLogFindDto;
import cloud.xcan.angus.core.gm.interfaces.operation.facade.dto.OperationLogSearchDto;
import cloud.xcan.angus.core.gm.interfaces.operation.facade.vo.OperationLogVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.List;


public interface OperationLogFacade {

  List<IdKey<Long, Object>> add(List<UserOperation> dto);

  PageResult<OperationLogVo> list(OperationLogFindDto dto);

  PageResult<OperationLogVo> search(OperationLogSearchDto dto);

}
