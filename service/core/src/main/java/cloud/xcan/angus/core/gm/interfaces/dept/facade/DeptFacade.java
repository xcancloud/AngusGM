package cloud.xcan.angus.core.gm.interfaces.dept.facade;

import cloud.xcan.angus.api.gm.dept.dto.DeptFindDto;
import cloud.xcan.angus.api.gm.dept.vo.DeptDetailVo;
import cloud.xcan.angus.api.gm.dept.vo.DeptListVo;
import cloud.xcan.angus.core.gm.domain.dept.DeptSubCount;
import cloud.xcan.angus.core.gm.interfaces.dept.facade.dto.DeptAddDto;
import cloud.xcan.angus.core.gm.interfaces.dept.facade.dto.DeptReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.dept.facade.dto.DeptUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.dept.facade.vo.DeptNavigationTreeVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.List;
import java.util.Set;

public interface DeptFacade {

  List<IdKey<Long, Object>> add(List<DeptAddDto> dto);

  void update(List<DeptUpdateDto> dto);

  List<IdKey<Long, Object>> replace(List<DeptReplaceDto> dto);

  DeptSubCount subCount(Long id);

  void delete(Set<Long> ids);

  DeptNavigationTreeVo navigation(Long id);

  DeptDetailVo detail(Long id);

  PageResult<DeptListVo> list(DeptFindDto dto);

}
