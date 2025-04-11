package cloud.xcan.angus.core.gm.interfaces.api.facade;


import cloud.xcan.angus.core.event.source.ApiLog;
import cloud.xcan.angus.core.gm.interfaces.api.facade.dto.ApiLogFindDto;
import cloud.xcan.angus.core.gm.interfaces.api.facade.vo.ApiLogDetailVo;
import cloud.xcan.angus.core.gm.interfaces.api.facade.vo.ApiLogInfoVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.List;


public interface ApiLogFacade {

  List<IdKey<Long, Object>> add(List<ApiLog> apiLogs);

  ApiLogDetailVo detail(Long id);

  PageResult<ApiLogInfoVo> list(ApiLogFindDto dto);

}
