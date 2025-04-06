package cloud.xcan.angus.core.gm.interfaces.tag.facade;

import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.OrgTagTargetAddDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.OrgTagTargetFindDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.OrgTagTargetDetailVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;


public interface OrgTagTargetFacade {

  List<IdKey<Long, Object>> targetAdd(Long id, LinkedHashSet<OrgTagTargetAddDto> dto);

  void targetDelete(Long id, HashSet<Long> ids);

  PageResult<OrgTagTargetDetailVo> targetList(OrgTagTargetFindDto dto);

}
