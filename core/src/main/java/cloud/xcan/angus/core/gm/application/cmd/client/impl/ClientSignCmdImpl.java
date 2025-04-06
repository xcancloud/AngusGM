package cloud.xcan.angus.core.gm.application.cmd.client.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.gm.application.converter.ClientSignConverter.convertClientSignInAuthentication;
import static cloud.xcan.angus.core.gm.application.converter.ClientSignConverter.privateSignupToDomain;
import static cloud.xcan.angus.core.gm.application.converter.UserSignConverter.convertClientSuccessAuthentication;
import static org.springframework.security.oauth2.core.AuthorizationGrantType.CLIENT_CREDENTIALS;

import cloud.xcan.angus.api.commonlink.AASConstant;
import cloud.xcan.angus.api.commonlink.client.Client2pSignupBiz;
import cloud.xcan.angus.api.commonlink.client.ClientAuth;
import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.cmd.client.ClientSignCmd;
import cloud.xcan.angus.core.gm.application.query.client.ClientQuery;
import cloud.xcan.angus.security.client.CustomOAuth2ClientRepository;
import cloud.xcan.angus.security.client.CustomOAuth2RegisteredClient;
import jakarta.annotation.Resource;
import java.util.Set;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;

@Slf4j
@Biz
public class ClientSignCmdImpl implements ClientSignCmd {

  @Resource
  private ClientQuery clientQuery;

  @Resource
  private CustomOAuth2ClientRepository customOAuth2ClientRepository;

  @Resource
  private AuthenticationManager authenticationManager;

  @Resource
  private PasswordEncoder passwordEncoder;

  @Override
  public OAuth2AccessToken signin(String clientId, String clientSecret, Set<String> scopes) {
    return new BizTemplate<OAuth2AccessToken>(false) {
      CustomOAuth2RegisteredClient clientDb;

      @Override
      protected void checkParams() {
        clientDb = clientQuery.checkAndFind(clientId, clientSecret, scopes);
        assertTrue(clientDb.getAuthorizationGrantTypes().contains(CLIENT_CREDENTIALS),
            "Unsupported client credentials grant type");
      }

      @Override
      protected OAuth2AccessToken process() {
        // Submit OAuth2 login authentication
        OAuth2ClientAuthenticationToken clientAuthenticationToken
            = convertClientSuccessAuthentication(clientDb);
        Authentication userAuthenticationToken = convertClientSignInAuthentication(
            scopes, clientAuthenticationToken);
        OAuth2AccessTokenAuthenticationToken result = (OAuth2AccessTokenAuthenticationToken)
            authenticationManager.authenticate(userAuthenticationToken);
        return result.getAccessToken();
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
        customOAuth2ClientRepository.save(client);
        return new ClientAuth().setClientId(clientId).setClientSecret(client.getClientSecret());
      }
    }.execute();
  }

}
