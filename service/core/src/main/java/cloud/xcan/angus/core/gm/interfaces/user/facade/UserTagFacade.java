package cloud.xcan.angus.core.gm.interfaces.user.facade;

import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.OrgTargetTagFindDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.OrgTagTargetVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;


public interface UserTagFacade {

  List<IdKey<Long, Object>> tagAdd(Long userId, LinkedHashSet<Long> tagIds);

  void tagReplace(Long userId, LinkedHashSet<Long> tagIds);

  void tagDelete(Long userId, HashSet<Long> tagIds);

  PageResult<OrgTagTargetVo> tagList(Long userId, OrgTargetTagFindDto dto);

}
