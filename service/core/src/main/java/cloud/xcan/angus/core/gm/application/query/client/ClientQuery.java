package cloud.xcan.angus.core.gm.application.query.client;

import cloud.xcan.angus.security.client.CustomOAuth2RegisteredClient;
import java.util.List;
import java.util.Set;


public interface ClientQuery {

  CustomOAuth2RegisteredClient detail(String id);

  List<CustomOAuth2RegisteredClient> find(String id, String clientId, String tenantId);

  CustomOAuth2RegisteredClient checkAndFind(String clientId);

  CustomOAuth2RegisteredClient checkAndFind(String clientId, String clientSecret);

  CustomOAuth2RegisteredClient checkAndFind(String clientId, String clientSecret, String scope);

  CustomOAuth2RegisteredClient checkAndFind(String clientId, boolean checkEnabled);

  CustomOAuth2RegisteredClient findValidByClientId0(String clientId);

}
