package cloud.xcan.angus.core.gm.interfaces.authentication.facade;

import cloud.xcan.angus.api.gm.auth.dto.UserTokenAddDto;
import cloud.xcan.angus.api.gm.auth.vo.UserTokenInfoVo;
import cloud.xcan.angus.api.gm.auth.vo.UserTokenValueVo;
import java.util.HashSet;
import java.util.List;

public interface AuthUserTokenFacade {

  UserTokenValueVo add(UserTokenAddDto dto);

  void delete(HashSet<Long> ids);

  UserTokenValueVo value(Long id);

  List<UserTokenInfoVo> list(String appCode);

}
