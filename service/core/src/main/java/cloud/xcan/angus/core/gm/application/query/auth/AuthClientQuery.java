package cloud.xcan.angus.core.gm.application.query.auth;

import cloud.xcan.angus.security.client.CustomOAuth2RegisteredClient;
import java.util.List;


public interface AuthClientQuery {

  CustomOAuth2RegisteredClient detail(String id);

  List<CustomOAuth2RegisteredClient> list(String id, String clientId, String tenantId);

  CustomOAuth2RegisteredClient checkAndFind(String clientId);

  CustomOAuth2RegisteredClient checkAndFind(String clientId, String clientSecret);

  CustomOAuth2RegisteredClient checkAndFind(String clientId, String clientSecret, String scope);

  CustomOAuth2RegisteredClient checkAndFind(String clientId, boolean checkEnabled);

  CustomOAuth2RegisteredClient findValidByClientId0(String clientId);

}
