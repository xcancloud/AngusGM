package cloud.xcan.angus.core.gm.interfaces.operation.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.operation.facade.internal.assembler.OperationLogAssembler.getSpecification;
import static cloud.xcan.angus.core.gm.interfaces.operation.facade.internal.assembler.OperationLogAssembler.toDomain;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.core.event.source.UserOperation;
import cloud.xcan.angus.core.gm.application.cmd.operation.OperationLogCmd;
import cloud.xcan.angus.core.gm.application.query.operation.OperationLogQuery;
import cloud.xcan.angus.core.gm.domain.operation.OperationLog;
import cloud.xcan.angus.core.gm.interfaces.operation.facade.OperationFacade;
import cloud.xcan.angus.core.gm.interfaces.operation.facade.dto.OperationFindDto;
import cloud.xcan.angus.core.gm.interfaces.operation.facade.internal.assembler.OperationLogAssembler;
import cloud.xcan.angus.core.gm.interfaces.operation.facade.vo.OperationLogVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;


@Component
public class OperationLogFacadeImpl implements OperationFacade {

  @Resource
  private OperationLogQuery operationLogQuery;

  @Resource
  private OperationLogCmd operationLogCmd;

  @Override
  public List<IdKey<Long, Object>> add(List<UserOperation> userOperations) {
    return operationLogCmd.add(toDomain(userOperations));
  }

  @Override
  public PageResult<OperationLogVo> list(OperationFindDto dto) {
    Page<OperationLog> page = operationLogQuery.list(getSpecification(dto), dto.tranPage());
    return buildVoPageResult(page, OperationLogAssembler::toVo);
  }

}
