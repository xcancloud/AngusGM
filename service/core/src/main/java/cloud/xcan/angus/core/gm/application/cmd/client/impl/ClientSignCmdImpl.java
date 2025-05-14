package cloud.xcan.angus.core.gm.application.cmd.client.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.gm.application.cmd.authuser.impl.AuthUserSignCmdImpl.sendOauth2RenewRequest;
import static cloud.xcan.angus.core.gm.application.converter.ClientSignConverter.privateSignupToDomain;
import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static org.springframework.security.oauth2.core.AuthorizationGrantType.CLIENT_CREDENTIALS;

import cloud.xcan.angus.api.commonlink.AASConstant;
import cloud.xcan.angus.api.commonlink.client.Client2pSignupBiz;
import cloud.xcan.angus.api.commonlink.client.ClientAuth;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.cmd.client.ClientSignCmd;
import cloud.xcan.angus.core.gm.application.query.client.ClientQuery;
import cloud.xcan.angus.remote.message.SysException;
import cloud.xcan.angus.security.client.CustomOAuth2ClientRepository;
import cloud.xcan.angus.security.client.CustomOAuth2RegisteredClient;
import jakarta.annotation.Resource;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Biz
public class ClientSignCmdImpl implements ClientSignCmd {

  @Resource
  private ClientQuery clientQuery;

  @Resource
  private CustomOAuth2ClientRepository customOAuth2ClientRepository;

  @Resource
  private PasswordEncoder passwordEncoder;

  @Override
  public Map<String, String> signin(String clientId, String clientSecret, String scope) {
    return new BizTemplate<Map<String, String>>(false) {
      CustomOAuth2RegisteredClient clientDb;

      @Override
      protected void checkParams() {
        clientDb = clientQuery.checkAndFind(clientId, clientSecret, scope);
        assertTrue(clientDb.getAuthorizationGrantTypes().contains(CLIENT_CREDENTIALS),
            "Unsupported client credentials grant type");
      }

      @Override
      protected Map<String, String> process() {
        // Submit OAuth2 login authentication
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

  @Override
  public ClientAuth signupByDoor(Client2pSignupBiz signupBiz, Long tenantId, String tenantName,
      Long resourceId) {
    return new BizTemplate<ClientAuth>() {
      @Override
      protected ClientAuth process() {
        String clientId = String.format(AASConstant.SIGN2P_CLIENT_ID_FMT, tenantId,
            signupBiz.name().toLowerCase(), resourceId);
        CustomOAuth2RegisteredClient clientDb = clientQuery.findValidByClientId0(clientId);
        if (clientDb != null) {
          // Re-acquire access authorization and update client authentication information
          String clientSecret = UUID.randomUUID().toString();
          clientDb.setClientSecret(passwordEncoder.encode(clientSecret));
          customOAuth2ClientRepository.save(clientDb);
          log.info("Re-acquire biz[{}-{}] client[{}] access authorization information, "
              + "client secret is updated", signupBiz.name(), resourceId, clientId);
          return new ClientAuth().setClientId(clientId).setClientSecret(clientSecret);
        }

        CustomOAuth2RegisteredClient client = privateSignupToDomain(
            clientId, signupBiz, tenantId, tenantName, resourceId);
        client.setClientSecret(passwordEncoder.encode(client.getClientSecret()));
        customOAuth2ClientRepository.save(client);
        return new ClientAuth().setClientId(clientId).setClientSecret(client.getClientSecret());
      }
    }.execute();
  }

  public static Map<String, String> submitOauth2ClientSignInRequest(String clientId,
      String clientSecret, String scope) throws Throwable {
    String authContent = format("client_id=%s&client_secret=%s&grant_type=%s", clientId,
        clientSecret, CLIENT_CREDENTIALS.getValue());
    if (isNotEmpty(scope)) {
      authContent = authContent + "&scope=" + scope;
    }
    return sendOauth2RenewRequest(authContent);
  }
}
