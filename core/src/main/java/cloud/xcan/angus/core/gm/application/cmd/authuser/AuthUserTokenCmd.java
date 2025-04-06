package cloud.xcan.angus.core.gm.application.cmd.authuser;

import cloud.xcan.angus.core.gm.domain.authuser.AuthUserToken;
import java.util.Set;

public interface AuthUserTokenCmd {

  AuthUserToken add(AuthUserToken userToken);

  void delete(Set<Long> ids);

}
