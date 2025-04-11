package cloud.xcan.angus.core.gm.interfaces.system.facade;

import cloud.xcan.angus.core.gm.interfaces.system.facade.dto.SystemTokenAddDto;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.SystemTokenDetailVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.SystemTokenInfoVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.SystemTokenValueVo;
import java.util.HashSet;
import java.util.List;


public interface SystemTokenFacade {

  SystemTokenValueVo add(SystemTokenAddDto dto);

  void delete(HashSet<Long> id);

  SystemTokenDetailVo auth(Long id);

  SystemTokenValueVo value(Long id);

  List<SystemTokenInfoVo> list();

}
