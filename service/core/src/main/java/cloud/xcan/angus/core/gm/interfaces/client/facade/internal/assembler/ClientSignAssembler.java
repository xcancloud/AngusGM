package cloud.xcan.angus.core.gm.interfaces.client.facade.internal.assembler;

import cloud.xcan.angus.api.commonlink.client.ClientAuth;
import cloud.xcan.angus.api.gm.client.vo.ClientSignVo;
import cloud.xcan.angus.api.gm.client.vo.ClientSignupVo;
import cloud.xcan.angus.spec.utils.JsonUtils;
import java.util.Map;

public class ClientSignAssembler {

  public static ClientSignVo signInToVo(Map<String, String> result) {
    return JsonUtils.fromJsonObject(result, ClientSignVo.class);
  }

  public static ClientSignupVo signup2Vo(ClientAuth signup2p) {
    return new ClientSignupVo().setClientId(signup2p.getClientId())
        .setClientSecret(signup2p.getClientSecret());
  }

}
