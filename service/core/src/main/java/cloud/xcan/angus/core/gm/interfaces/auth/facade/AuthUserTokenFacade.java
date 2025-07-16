package cloud.xcan.angus.core.gm.interfaces.auth.facade;

import cloud.xcan.angus.core.gm.interfaces.auth.facade.dto.token.UserTokenAddDto;
import cloud.xcan.angus.core.gm.interfaces.auth.facade.vo.token.UserTokenInfoVo;
import cloud.xcan.angus.core.gm.interfaces.auth.facade.vo.token.UserTokenValueVo;
import java.util.HashSet;
import java.util.List;


public interface AuthUserTokenFacade {

  UserTokenValueVo add(UserTokenAddDto dto);

  void delete(HashSet<Long> ids);

  UserTokenValueVo value(Long id);

  List<UserTokenInfoVo> list();

}
