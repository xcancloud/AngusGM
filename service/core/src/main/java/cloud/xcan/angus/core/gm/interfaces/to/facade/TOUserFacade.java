package cloud.xcan.angus.core.gm.interfaces.to.facade;


import cloud.xcan.angus.core.gm.interfaces.to.facade.dto.TOUserAddDto;
import cloud.xcan.angus.core.gm.interfaces.to.facade.dto.TOUserFindDto;
import cloud.xcan.angus.core.gm.interfaces.to.facade.vo.TOUserDetailVo;
import cloud.xcan.angus.core.gm.interfaces.to.facade.vo.TOUserVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;
import java.util.List;

public interface TOUserFacade {

  List<IdKey<Long, Object>> add(List<TOUserAddDto> dto);

  void delete(HashSet<Long> ids);

  TOUserDetailVo detail(Long userId);

  PageResult<TOUserVo> list(TOUserFindDto dto);

}
