package cloud.xcan.angus.core.gm.interfaces.api.facade;

import cloud.xcan.angus.core.gm.interfaces.api.facade.dto.ApiAddDto;
import cloud.xcan.angus.core.gm.interfaces.api.facade.dto.ApiFindDto;
import cloud.xcan.angus.core.gm.interfaces.api.facade.dto.ApiReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.api.facade.dto.ApiSearchDto;
import cloud.xcan.angus.core.gm.interfaces.api.facade.dto.ApiUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.api.facade.vo.ApiDetailVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.remote.dto.EnabledOrDisabledDto;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;
import java.util.List;

public interface ApiFacade {

  List<IdKey<Long, Object>> add(List<ApiAddDto> dto);

  void update(List<ApiUpdateDto> dto);

  List<IdKey<Long, Object>> replace(List<ApiReplaceDto> dto);

  void delete(HashSet<Long> ids);

  void enabled(List<EnabledOrDisabledDto> dto);

  ApiDetailVo detail(Long id);

  PageResult<ApiDetailVo> list(ApiFindDto dto);

  PageResult<ApiDetailVo> search(ApiSearchDto dto);

}
