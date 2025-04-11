package cloud.xcan.angus.core.gm.interfaces.group.facade;

import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.OrgTargetTagFindDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.OrgTagTargetVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;


public interface GroupTagFacade {

  List<IdKey<Long, Object>> tagAdd(Long groupId, LinkedHashSet<Long> tagIds);

  void tagReplace(Long groupId, LinkedHashSet<Long> userIds);

  void tagDelete(Long groupId, HashSet<Long> tagIds);

  PageResult<OrgTagTargetVo> tagList(Long groupId, OrgTargetTagFindDto dto);

}
