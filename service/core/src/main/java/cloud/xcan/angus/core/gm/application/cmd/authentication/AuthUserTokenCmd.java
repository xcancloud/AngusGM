package cloud.xcan.angus.core.gm.application.cmd.authentication;

import cloud.xcan.angus.core.gm.domain.authentication.AuthUserToken;
import java.util.Set;

public interface AuthUserTokenCmd {

  AuthUserToken add(AuthUserToken userToken);

  void delete(Set<Long> ids);

}
