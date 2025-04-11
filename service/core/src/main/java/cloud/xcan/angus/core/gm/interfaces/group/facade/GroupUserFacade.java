package cloud.xcan.angus.core.gm.interfaces.group.facade;

import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.user.GroupUserFindDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.group.UserGroupVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;


public interface GroupUserFacade {

  List<IdKey<Long, Object>> userAdd(Long groupId, LinkedHashSet<Long> userIds);

  void userDelete(Long groupId, HashSet<Long> userIds);

  PageResult<UserGroupVo> groupUserList(Long groupId, GroupUserFindDto dto);

}
