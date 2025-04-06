package cloud.xcan.angus.core.gm.interfaces.group.facade;

import cloud.xcan.angus.api.gm.group.dto.GroupFindDto;
import cloud.xcan.angus.api.gm.group.dto.GroupSearchDto;
import cloud.xcan.angus.api.gm.group.vo.GroupDetailVo;
import cloud.xcan.angus.api.gm.group.vo.GroupListVo;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupAddDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupUpdateDto;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.remote.dto.EnabledOrDisabledDto;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface GroupFacade {

  List<IdKey<Long, Object>> add(List<GroupAddDto> dto);

  void update(List<GroupUpdateDto> dto);

  List<IdKey<Long, Object>> replace(List<GroupReplaceDto> dto);

  void delete(HashSet<Long> ids);

  void enabled(Set<EnabledOrDisabledDto> dto);

  GroupDetailVo detail(Long id);

  PageResult<GroupListVo> list(GroupFindDto dto);

  PageResult<GroupListVo> search(GroupSearchDto dto);

}
