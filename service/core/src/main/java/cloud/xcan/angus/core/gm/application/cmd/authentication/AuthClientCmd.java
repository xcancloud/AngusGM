package cloud.xcan.angus.core.gm.application.cmd.authentication;

import cloud.xcan.angus.api.commonlink.client.ClientSource;
import cloud.xcan.angus.security.client.CustomOAuth2RegisteredClient;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;


public interface AuthClientCmd {

  IdKey<String, Object> add(CustomOAuth2RegisteredClient client);

  void update(CustomOAuth2RegisteredClient client);

  IdKey<String, Object> replace(CustomOAuth2RegisteredClient client);

  void delete(HashSet<String> clientIds);

  void deleteSystemTokenClient(String tokenName, ClientSource source);

}
