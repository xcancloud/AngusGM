package cloud.xcan.angus.core.gm.interfaces.app.facade;

import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.tag.AppTargetTagFindDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.tag.AppTagTargetVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

public interface AppFuncTagFacade {

  List<IdKey<Long, Object>> funcTagAdd(Long funcId, LinkedHashSet<Long> tagIds);

  void funcTagReplace(Long id, LinkedHashSet<Long> ids);

  void funcTagDelete(Long funcId, HashSet<Long> tagIds);

  PageResult<AppTagTargetVo> funcTagList(Long funcId, AppTargetTagFindDto dto);
}
