package cloud.xcan.angus.core.gm.interfaces.auth.facade.internal.assembler;

import cloud.xcan.angus.api.commonlink.client.ClientAuth;
import cloud.xcan.angus.api.gm.client.vo.AuthClientSignVo;
import cloud.xcan.angus.api.gm.client.vo.AuthClientSignupVo;
import cloud.xcan.angus.spec.utils.JsonUtils;
import java.util.Map;

public class AuthClientSignAssembler {

  public static AuthClientSignVo signInToVo(Map<String, String> result) {
    return JsonUtils.fromJsonObject(result, AuthClientSignVo.class);
  }

  public static AuthClientSignupVo signup2Vo(ClientAuth signup2p) {
    return new AuthClientSignupVo().setClientId(signup2p.getClientId())
        .setClientSecret(signup2p.getClientSecret());
  }

}
