package cloud.xcan.angus.core.gm.interfaces.app.facade;

import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.func.AppFuncAddDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.func.AppFuncFindDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.func.AppFuncReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.func.AppFuncUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.func.AppFuncDetailVo;
import cloud.xcan.angus.api.gm.app.vo.AppFuncTreeVo;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.func.AppFuncVo;
import cloud.xcan.angus.remote.dto.EnabledOrDisabledDto;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;
import java.util.List;

public interface AppFuncFacade {

  List<IdKey<Long, Object>> add(Long appId, List<AppFuncAddDto> dto);

  void update(Long appId, List<AppFuncUpdateDto> dto);

  void replace(Long appId, List<AppFuncReplaceDto> dto);

  void delete(Long appId, HashSet<Long> ids);

  void enabled(Long appId, List<EnabledOrDisabledDto> dto);

  AppFuncDetailVo detail(Long id);

  List<AppFuncVo> list(Long appId, AppFuncFindDto dto);

  List<AppFuncTreeVo> tree(Long appId, AppFuncFindDto dto);

}
