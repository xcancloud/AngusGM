package cloud.xcan.angus.core.gm.interfaces.to.facade;

import cloud.xcan.angus.core.gm.interfaces.to.facade.dto.TORoleAddDto;
import cloud.xcan.angus.core.gm.interfaces.to.facade.dto.TORoleFindDto;
import cloud.xcan.angus.core.gm.interfaces.to.facade.dto.TORoleReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.to.facade.dto.TORoleUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.to.facade.vo.TORoleDetailVo;
import cloud.xcan.angus.core.gm.interfaces.to.facade.vo.TORoleVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.remote.dto.EnabledOrDisabledDto;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;
import java.util.List;


public interface TORoleFacade {

  List<IdKey<Long, Object>> add(List<TORoleAddDto> dto);

  void update(List<TORoleUpdateDto> dto);

  List<IdKey<Long, Object>> replace(List<TORoleReplaceDto> dto);

  void delete(HashSet<Long> ids);

  void enabled(List<EnabledOrDisabledDto> dto);

  TORoleDetailVo detail(String idOrCode);

  PageResult<TORoleVo> list(TORoleFindDto dto);

}
