package cloud.xcan.angus.core.gm.application.cmd.auth.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.gm.application.cmd.auth.impl.AuthUserSignCmdImpl.sendOauth2Request;
import static cloud.xcan.angus.core.gm.application.converter.AuthClientSignConverter.privateSignupToDomain;
import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static org.springframework.security.oauth2.core.AuthorizationGrantType.CLIENT_CREDENTIALS;

import cloud.xcan.angus.api.commonlink.AuthConstant;
import cloud.xcan.angus.api.commonlink.client.Client2pSignupBiz;
import cloud.xcan.angus.api.commonlink.client.ClientAuth;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.cmd.auth.AuthClientSignCmd;
import cloud.xcan.angus.core.gm.application.query.auth.AuthClientQuery;
import cloud.xcan.angus.remote.message.SysException;
import cloud.xcan.angus.security.client.CustomOAuth2ClientRepository;
import cloud.xcan.angus.security.client.CustomOAuth2RegisteredClient;
import jakarta.annotation.Resource;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Implementation of OAuth2 client sign-in command operations.
 *
 * <p>This class provides comprehensive functionality for OAuth2 client authentication
 * including:</p>
 * <ul>
 *   <li>Client credentials authentication for OAuth2 clients</li>
 *   <li>Private client registration for business operations</li>
 *   <li>Client secret management and renewal</li>
 *   <li>OAuth2 token generation for client authentication</li>
 * </ul>
 *
 * <p>The implementation supports both standard OAuth2 client authentication
 * and private client registration for internal business operations.</p>
 */
@Slf4j
@org.springframework.stereotype.Service
public class AuthClientSignCmdImpl implements AuthClientSignCmd {

  @Resource
  private AuthClientQuery authClientQuery;
  @Resource
  private CustomOAuth2ClientRepository customOAuth2ClientRepository;
  @Resource
  private PasswordEncoder passwordEncoder;

  /**
   * Authenticates OAuth2 client using client credentials grant type.
   *
   * <p>This method performs client authentication including:</p>
   * <ul>
   *   <li>Validating client credentials and scope</li>
   *   <li>Checking client credentials grant type support</li>
   *   <li>Submitting OAuth2 authentication request</li>
   *   <li>Returning access token and related information</li>
   * </ul>
   *
   * @param clientId     OAuth2 client identifier
   * @param clientSecret OAuth2 client secret
   * @param scope        Requested OAuth2 scope
   * @return Map containing access token and related OAuth2 response data
   */
  @Override
  public Map<String, String> signin(String clientId, String clientSecret, String scope) {
    return new BizTemplate<Map<String, String>>(false) {
      CustomOAuth2RegisteredClient clientDb;

      @Override
      protected void checkParams() {
        // Validate client credentials and scope
        clientDb = authClientQuery.checkAndFind(clientId, clientSecret, scope);
        // Ensure client supports client credentials grant type
        assertTrue(clientDb.getAuthorizationGrantTypes().contains(CLIENT_CREDENTIALS),
            "Unsupported client credentials grant type");
      }

      @Override
      protected Map<String, String> process() {
        // Submit OAuth2 client authentication request
        try {
          return submitOauth2ClientSignInRequest(clientId, clientSecret, scope);
        } catch (Throwable e) {
          String cause = nonNull(e.getCause()) ? e.getCause().getMessage() : e.getMessage();
          log.error(cause, e);
          throw new SysException(cause);
        }
      }
    }.execute();
  }

  /**
   * Registers private OAuth2 client for business operations.
   *
   * <p>This method handles private client registration including:</p>
   * <ul>
   *   <li>Generating client ID based on tenant and business parameters</li>
   *   <li>Creating or updating client with new credentials</li>
   *   <li>Managing client secret renewal for existing clients</li>
   *   <li>Returning client authentication information</li>
   * </ul>
   *
   * @param signupBiz  Business operation type for client registration
   * @param tenantId   Tenant identifier
   * @param tenantName Tenant name
   * @param resourceId Resource identifier
   * @return Client authentication information with credentials
   */
  @Override
  public ClientAuth signupByDoor(Client2pSignupBiz signupBiz, Long tenantId, String tenantName,
      Long resourceId) {
    return new BizTemplate<ClientAuth>() {
      @Override
      protected ClientAuth process() {
        // Generate client ID based on business parameters
        String clientId = String.format(AuthConstant.SIGN2P_CLIENT_ID_FMT, tenantId,
            signupBiz.name().toLowerCase(), resourceId);
        CustomOAuth2RegisteredClient clientDb = authClientQuery.findValidByClientId0(clientId);

        if (clientDb != null) {
          // Renew access authorization and update client authentication information
          String clientSecret = UUID.randomUUID().toString();
          clientDb.setClientSecret(passwordEncoder.encode(clientSecret));
          customOAuth2ClientRepository.save(clientDb);
          log.info("Re-acquire biz[{}-{}] client[{}] access authorization information, "
              + "client secret is updated", signupBiz.name(), resourceId, clientId);
          return new ClientAuth().setClientId(clientId).setClientSecret(clientSecret);
        }

        // Create new private client
        CustomOAuth2RegisteredClient client = privateSignupToDomain(
            clientId, signupBiz, tenantId, tenantName, resourceId);
        client.setClientSecret(passwordEncoder.encode(client.getClientSecret()));
        customOAuth2ClientRepository.save(client);
        return new ClientAuth().setClientId(clientId).setClientSecret(client.getClientSecret());
      }
    }.execute();
  }

  /**
   * Submits OAuth2 client sign-in request to authorization server.
   *
   * <p>This method constructs and sends OAuth2 client credentials request
   * to the authorization server for token generation.</p>
   *
   * @param clientId     OAuth2 client identifier
   * @param clientSecret OAuth2 client secret
   * @param scope        Requested OAuth2 scope
   * @return Map containing OAuth2 response data
   * @throws Throwable if authentication request fails
   */
  public static Map<String, String> submitOauth2ClientSignInRequest(String clientId,
      String clientSecret, String scope) throws Throwable {
    // Construct OAuth2 client credentials request
    String authContent = format("client_id=%s&client_secret=%s&grant_type=%s", clientId,
        clientSecret, CLIENT_CREDENTIALS.getValue());
    if (isNotEmpty(scope)) {
      authContent = authContent + "&scope=" + scope;
    }
    return sendOauth2Request(authContent);
  }
}
