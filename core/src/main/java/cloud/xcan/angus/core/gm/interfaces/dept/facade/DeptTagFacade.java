package cloud.xcan.angus.core.gm.interfaces.dept.facade;

import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.OrgTargetTagFindDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.OrgTagTargetVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;


public interface DeptTagFacade {

  List<IdKey<Long, Object>> tagAdd(Long deptId, LinkedHashSet<Long> tagIds);

  void tagReplace(Long deptId, LinkedHashSet<Long> ids);

  void tagDelete(Long deptId, HashSet<Long> tagIds);

  PageResult<OrgTagTargetVo> tagList(Long deptId, OrgTargetTagFindDto dto);

}
