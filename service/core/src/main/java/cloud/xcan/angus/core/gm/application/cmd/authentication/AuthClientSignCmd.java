package cloud.xcan.angus.core.gm.application.cmd.authentication;

import cloud.xcan.angus.api.commonlink.client.Client2pSignupBiz;
import cloud.xcan.angus.api.commonlink.client.ClientAuth;
import java.util.Map;

public interface AuthClientSignCmd {

  Map<String, String> signin(String clientId, String clientSecret, String scope);

  ClientAuth signupByDoor(Client2pSignupBiz signupBiz, Long tenantId, String tenantName,
      Long resourceId);
}
