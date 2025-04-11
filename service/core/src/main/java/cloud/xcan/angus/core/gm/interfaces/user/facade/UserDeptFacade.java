package cloud.xcan.angus.core.gm.interfaces.user.facade;

import cloud.xcan.angus.api.gm.user.to.UserDeptTo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.dept.UserDeptFindDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.dept.UserDeptVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;


public interface UserDeptFacade {

  List<IdKey<Long, Object>> deptAdd(Long userId, LinkedHashSet<Long> deptIds);

  void deptReplace(Long id, LinkedHashSet<UserDeptTo> dto);

  void deptDelete(Long userId, HashSet<Long> deptIds);

  PageResult<UserDeptVo> deptList(Long userId, UserDeptFindDto dto);

}
