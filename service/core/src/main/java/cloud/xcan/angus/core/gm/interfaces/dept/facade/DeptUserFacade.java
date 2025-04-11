package cloud.xcan.angus.core.gm.interfaces.dept.facade;

import cloud.xcan.angus.core.gm.interfaces.dept.facade.dto.DeptHeadReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.dept.facade.dto.user.DeptUserFindDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.dept.UserDeptVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;


public interface DeptUserFacade {

  List<IdKey<Long, Object>> userAdd(Long id, LinkedHashSet<Long> ids);

  void userDelete(Long deptId, HashSet<Long> userIds);

  void headReplace(Long deptId, DeptHeadReplaceDto dto);

  PageResult<UserDeptVo> deptUserList(Long deptId, DeptUserFindDto dto);

}
