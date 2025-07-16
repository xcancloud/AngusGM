package cloud.xcan.angus.core.gm.application.cmd.auth;

import cloud.xcan.angus.core.gm.domain.auth.AuthUserToken;
import java.util.Set;

public interface AuthUserTokenCmd {

  AuthUserToken add(AuthUserToken userToken);

  void delete(Set<Long> ids);

}
