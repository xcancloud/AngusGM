package cloud.xcan.angus.core.gm.application.cmd.client;

import cloud.xcan.angus.api.commonlink.client.Client2pSignupBiz;
import cloud.xcan.angus.api.commonlink.client.ClientAuth;
import java.util.Set;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

public interface ClientSignCmd {

  OAuth2AccessToken signin(String clientId, String clientSecret, String scope);

  ClientAuth signupByDoor(Client2pSignupBiz signupBiz, Long tenantId, String tenantName,
      Long resourceId);
}
