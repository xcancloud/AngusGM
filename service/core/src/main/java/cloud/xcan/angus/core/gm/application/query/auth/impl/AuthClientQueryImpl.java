package cloud.xcan.angus.core.gm.application.query.auth.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceNotFound;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.gm.domain.AuthMessage.CLIENT_IS_DISABLED_T;
import static cloud.xcan.angus.remote.message.http.Unauthorized.M.INVALID_CLIENT;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.auth.AuthClientQuery;
import cloud.xcan.angus.security.client.CustomOAuth2ClientRepository;
import cloud.xcan.angus.security.client.CustomOAuth2RegisteredClient;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.util.StringUtils;

/**
 * <p>
 * Implementation of OAuth2 client query operations.
 * </p>
 * <p>
 * Manages OAuth2 client retrieval, validation, and authentication. Provides comprehensive client
 * querying with scope validation support.
 * </p>
 * <p>
 * Supports client detail retrieval, authentication validation, scope checking, and enabled status
 * verification for OAuth2 client management.
 * </p>
 */
@org.springframework.stereotype.Service
public class AuthClientQueryImpl implements AuthClientQuery {

  @Resource
  private CustomOAuth2ClientRepository customOAuth2ClientRepository;
  @Resource
  private PasswordEncoder passwordEncoder;

  /**
   * <p>
   * Retrieves detailed OAuth2 client information by ID.
   * </p>
   * <p>
   * Fetches complete client record with all associated information. Throws ResourceNotFound
   * exception if client does not exist.
   * </p>
   */
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

  /**
   * <p>
   * Retrieves OAuth2 clients with optional filtering.
   * </p>
   * <p>
   * Supports filtering by ID, client ID, and tenant ID. Builds dynamic SQL filter based on provided
   * parameters.
   * </p>
   */
  @Override
  public List<CustomOAuth2RegisteredClient> list(String id, String clientId, String tenantId) {
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

  /**
   * <p>
   * Validates and retrieves OAuth2 client by client ID.
   * </p>
   * <p>
   * Verifies client exists and returns client information. Throws ResourceNotFound exception if
   * client does not exist.
   * </p>
   */
  @Override
  public CustomOAuth2RegisteredClient checkAndFind(String clientId) {
    RegisteredClient client = customOAuth2ClientRepository.findByClientId(clientId);
    assertResourceNotFound(client, clientId, "Client");
    return (CustomOAuth2RegisteredClient) client;
  }

  /**
   * <p>
   * Validates OAuth2 client authentication with client secret.
   * </p>
   * <p>
   * Verifies client exists and validates client secret using password encoder. Throws Unauthorized
   * exception if authentication fails.
   * </p>
   */
  @Override
  public CustomOAuth2RegisteredClient checkAndFind(String clientId, String clientSecret) {
    CustomOAuth2RegisteredClient client = checkAndFind(clientId);
    assertTrue(passwordEncoder.matches(clientSecret, client.getClientSecret()), INVALID_CLIENT);
    return client;
  }

  /**
   * <p>
   * Validates OAuth2 client authentication with scope validation.
   * </p>
   * <p>
   * Verifies client exists, validates client secret, and checks requested scopes. Throws
   * appropriate exceptions for authentication or scope validation failures.
   * </p>
   */
  @Override
  public CustomOAuth2RegisteredClient checkAndFind(String clientId, String clientSecret,
      @Nullable String scope) {
    CustomOAuth2RegisteredClient client = checkAndFind(clientId, clientSecret);
    if (scope != null) {
      Set<String> requestedScopes = new HashSet<>(
          Arrays.asList(StringUtils.delimitedListToStringArray(scope, " ")));
      for (String scope0 : requestedScopes) {
        assertTrue(client.getScopes().contains(scope0),
            String.format("Client scope %s is invalid", scope0));
      }
    }
    return client;
  }

  /**
   * <p>
   * Validates and retrieves OAuth2 client with optional enabled status check.
   * </p>
   * <p>
   * Verifies client exists and optionally checks enabled status. Throws appropriate exceptions for
   * missing or disabled clients.
   * </p>
   */
  @Override
  public CustomOAuth2RegisteredClient checkAndFind(String clientId, boolean checkEnabled) {
    CustomOAuth2RegisteredClient client = checkAndFind(clientId);
    if (checkEnabled) {
      assertTrue(client.isEnabled(), CLIENT_IS_DISABLED_T, new Object[]{client.getClientId()});
    }
    return client;
  }

  /**
   * <p>
   * Retrieves valid OAuth2 client by client ID without exceptions.
   * </p>
   * <p>
   * Returns client if it exists and is enabled, otherwise returns null. Used for non-critical
   * client lookups.
   * </p>
   */
  @Override
  public CustomOAuth2RegisteredClient findValidByClientId0(String clientId) {
    RegisteredClient client = customOAuth2ClientRepository.findByClientId(clientId);
    return nonNull(client) && ((CustomOAuth2RegisteredClient) client).isEnabled()
        ? (CustomOAuth2RegisteredClient) client : null;
  }

}
