package cloud.xcan.angus.core.gm.interfaces.app.facade;

import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.tag.AppTargetTagFindDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.tag.AppTagTargetVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

public interface AppTagFacade {

  List<IdKey<Long, Object>> appTagAdd(Long appId, LinkedHashSet<Long> tagIds);

  void appTagReplace(Long id, LinkedHashSet<Long> ids);

  void appTagDelete(Long appId, HashSet<Long> tagIds);

  PageResult<AppTagTargetVo> appTagList(Long appId, AppTargetTagFindDto dto);
}
