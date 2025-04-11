package cloud.xcan.angus.core.gm.interfaces.user.facade;

import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.group.UserGroupFindDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.group.UserGroupVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;


public interface UserGroupFacade {

  List<IdKey<Long, Object>> groupAdd(Long userId, LinkedHashSet<Long> groupIds);

  void groupReplace(Long userId, LinkedHashSet<Long> groupIds);

  void groupDelete(Long userId, HashSet<Long> groupIds);

  PageResult<UserGroupVo> groupList(Long userId, UserGroupFindDto dto);

}
