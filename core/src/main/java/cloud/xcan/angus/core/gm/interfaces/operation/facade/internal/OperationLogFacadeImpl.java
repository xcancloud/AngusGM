package cloud.xcan.angus.core.gm.interfaces.operation.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.operation.facade.internal.assembler.OperationLogAssembler.getSearchCriteria;
import static cloud.xcan.angus.core.gm.interfaces.operation.facade.internal.assembler.OperationLogAssembler.getSpecification;
import static cloud.xcan.angus.core.gm.interfaces.operation.facade.internal.assembler.OperationLogAssembler.toDomain;
import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.core.event.source.UserOperation;
import cloud.xcan.angus.core.gm.application.cmd.operation.OperationLogCmd;
import cloud.xcan.angus.core.gm.application.query.operation.OperationLogQuery;
import cloud.xcan.angus.core.gm.application.query.operation.OperationLogSearch;
import cloud.xcan.angus.core.gm.domain.operation.OperationLog;
import cloud.xcan.angus.core.gm.interfaces.operation.facade.OperationLogFacade;
import cloud.xcan.angus.core.gm.interfaces.operation.facade.dto.OperationLogFindDto;
import cloud.xcan.angus.core.gm.interfaces.operation.facade.dto.OperationLogSearchDto;
import cloud.xcan.angus.core.gm.interfaces.operation.facade.internal.assembler.OperationLogAssembler;
import cloud.xcan.angus.core.gm.interfaces.operation.facade.vo.OperationLogVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;


@Component
public class OperationLogFacadeImpl implements OperationLogFacade {

  @Resource
  private OperationLogQuery operationLogQuery;

  @Resource
  private OperationLogSearch operationLogSearch;

  @Resource
  private OperationLogCmd operationLogCmd;

  @Override
  public List<IdKey<Long, Object>> add(List<UserOperation> userOperations) {
    return operationLogCmd.add(toDomain(userOperations));
  }

  @Override
  public PageResult<OperationLogVo> list(OperationLogFindDto dto) {
    Page<OperationLog> page = operationLogQuery.list(getSpecification(dto), dto.tranPage());
    return buildVoPageResult(page, OperationLogAssembler::toVo);
  }

  @Override
  public PageResult<OperationLogVo> search(OperationLogSearchDto dto) {
    Page<OperationLog> page = operationLogSearch.search(getSearchCriteria(dto), dto.tranPage(),
        OperationLog.class, getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, OperationLogAssembler::toVo);
  }

}
