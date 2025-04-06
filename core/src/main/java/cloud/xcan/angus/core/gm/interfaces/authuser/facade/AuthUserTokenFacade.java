package cloud.xcan.angus.core.gm.interfaces.authuser.facade;

import cloud.xcan.angus.core.gm.interfaces.authuser.facade.dto.token.UserTokenAddDto;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.vo.token.UserTokenInfoVo;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.vo.token.UserTokenValueVo;
import java.util.HashSet;
import java.util.List;


public interface AuthUserTokenFacade {

  UserTokenValueVo add(UserTokenAddDto dto);

  void delete(HashSet<Long> ids);

  UserTokenValueVo value(Long id);

  List<UserTokenInfoVo> list();

}
