package cloud.xcan.angus.core.gm.application.query.client.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceNotFound;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.gm.domain.AASCoreMessage.CLIENT_IS_DISABLED_T;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.client.ClientQuery;
import cloud.xcan.angus.security.client.CustomOAuth2ClientRepository;
import cloud.xcan.angus.security.client.CustomOAuth2RegisteredClient;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

@Biz
public class ClientQueryImpl implements ClientQuery {

  @Resource
  private CustomOAuth2ClientRepository customOAuth2ClientRepository;

  @Override
  public CustomOAuth2RegisteredClient detail(String id) {
    return new BizTemplate<CustomOAuth2RegisteredClient>() {

      @Override
      protected CustomOAuth2RegisteredClient process() {
        RegisteredClient client = customOAuth2ClientRepository.findById(id);
        assertResourceNotFound(client, id, "Client");
        return (CustomOAuth2RegisteredClient) client;
      }
    }.execute();
  }

  @Override
  public List<CustomOAuth2RegisteredClient> find(String id, String clientId, String tenantId) {
    return new BizTemplate<List<CustomOAuth2RegisteredClient>>() {

      @Override
      protected List<CustomOAuth2RegisteredClient> process() {
        List<String> args = new ArrayList<>();
        StringBuilder filter = new StringBuilder(" 1 = 1 ");
        if (isNotEmpty(id)) {
          args.add(id);
          filter.append(" AND id = ").append(id).append(" ");
        }
        if (isNotEmpty(clientId)) {
          args.add(clientId);
          filter.append(" AND client_id = ").append(clientId).append(" ");
        }
        if (isNotEmpty(tenantId)) {
          args.add(tenantId);
          filter.append(" AND tenant_id = ").append(tenantId).append(" ");
        }
        return customOAuth2ClientRepository.findAllBy(filter.toString(),
            args.toArray(new String[0]));
      }
    }.execute();
  }

  @Override
  public CustomOAuth2RegisteredClient checkAndFind(String clientId) {
    RegisteredClient client = customOAuth2ClientRepository.findById(clientId);
    assertResourceNotFound(client, clientId, "Client");
    return (CustomOAuth2RegisteredClient) client;
  }

  @Override
  public CustomOAuth2RegisteredClient checkAndFind(String clientId, String clientSecret) {
    CustomOAuth2RegisteredClient client = checkAndFind(clientId);
    assertTrue(clientSecret.equals(client.getClientSecret()), "Client secret inconsistency");
    return client;
  }

  @Override
  public CustomOAuth2RegisteredClient checkAndFind(String clientId, String clientSecret,
      Set<String> scopes) {
    CustomOAuth2RegisteredClient client = checkAndFind(clientId, clientSecret);
    for (String scope : scopes) {
      assertTrue(client.getScopes().contains(scope),
          String.format("Client scope %s is invalid", scope));
    }
    return client;
  }

  @Override
  public CustomOAuth2RegisteredClient checkAndFind(String clientId, boolean checkEnabled) {
    CustomOAuth2RegisteredClient client = checkAndFind(clientId);
    if (checkEnabled) {
      assertTrue(client.isEnabled(), CLIENT_IS_DISABLED_T, new Object[]{client.getClientId()});
    }
    return client;
  }

  @Override
  public CustomOAuth2RegisteredClient findValidByClientId0(String clientId) {
    RegisteredClient client = customOAuth2ClientRepository.findById(clientId);
    return nonNull(client) && ((CustomOAuth2RegisteredClient) client).isEnabled()
        ? (CustomOAuth2RegisteredClient) client : null;
  }

}
