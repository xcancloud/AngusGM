package cloud.xcan.angus.core.gm.interfaces.api.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.api.facade.internal.assembler.ApiLogAssembler.getSpecification;
import static cloud.xcan.angus.core.gm.interfaces.api.facade.internal.assembler.ApiLogAssembler.toDetailVo;
import static cloud.xcan.angus.core.gm.interfaces.api.facade.internal.assembler.ApiLogAssembler.toDomain;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.core.event.source.ApiLog;
import cloud.xcan.angus.core.gm.application.cmd.api.ApiLogCmd;
import cloud.xcan.angus.core.gm.application.query.api.ApiLogsQuery;
import cloud.xcan.angus.core.gm.domain.api.log.ApiLogInfo;
import cloud.xcan.angus.core.gm.interfaces.api.facade.ApiLogFacade;
import cloud.xcan.angus.core.gm.interfaces.api.facade.dto.ApiLogFindDto;
import cloud.xcan.angus.core.gm.interfaces.api.facade.internal.assembler.ApiLogAssembler;
import cloud.xcan.angus.core.gm.interfaces.api.facade.vo.ApiLogDetailVo;
import cloud.xcan.angus.core.gm.interfaces.api.facade.vo.ApiLogInfoVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;


@Component
public class ApiLogFacadeImpl implements ApiLogFacade {

  @Resource
  private ApiLogsQuery requestLogQuery;

  @Resource
  private ApiLogCmd requestLogCmd;

  @Override
  public List<IdKey<Long, Object>> add(List<ApiLog> apiLogs) {
    return requestLogCmd.add(toDomain(apiLogs));
  }

  @Override
  public ApiLogDetailVo detail(Long id) {
    return toDetailVo(requestLogQuery.detail(id));
  }

  @Override
  public PageResult<ApiLogInfoVo> list(ApiLogFindDto dto) {
    Page<ApiLogInfo> page = requestLogQuery.list(getSpecification(dto), dto.tranPage());
    return buildVoPageResult(page, ApiLogAssembler::toVo);
  }

}
