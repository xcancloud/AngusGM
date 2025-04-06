package cloud.xcan.angus.core.gm.interfaces.client.facade.internal.assembler;

import cloud.xcan.angus.api.commonlink.client.ClientAuth;
import cloud.xcan.angus.api.gm.client.vo.ClientSignVo;
import cloud.xcan.angus.api.gm.client.vo.ClientSignupVo;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

public class ClientSignAssembler {

  public static ClientSignVo signInToVo(OAuth2AccessToken accessToken) {
    return new ClientSignVo()
        .setAccessToken(accessToken.getTokenValue())
        .setExpiresAt(accessToken.getExpiresAt())
        .setTokenType(accessToken.getTokenType().getValue())
        .setScopes(accessToken.getScopes());
  }

  public static ClientSignupVo signup2Vo(ClientAuth signup2p) {
    return new ClientSignupVo().setClientId(signup2p.getClientId())
        .setClientSecret(signup2p.getClientSecret());
  }

}
