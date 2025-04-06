package cloud.xcan.angus.core.gm.interfaces.tag.facade;

import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.WebTagTargetAddDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.WebTagTargetFindDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.WebTagTargetDetailVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;


public interface WebTagTargetFacade {

  List<IdKey<Long, Object>> targetAdd(Long id, LinkedHashSet<WebTagTargetAddDto> dto);

  void targetDelete(Long id, HashSet<Long> ids);

  PageResult<WebTagTargetDetailVo> targetList(WebTagTargetFindDto dto);

}
