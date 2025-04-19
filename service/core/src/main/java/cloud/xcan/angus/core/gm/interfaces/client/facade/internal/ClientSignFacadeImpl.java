package cloud.xcan.angus.core.gm.interfaces.client.facade.internal;


import static cloud.xcan.angus.core.gm.interfaces.client.facade.internal.assembler.ClientSignAssembler.signInToVo;
import static cloud.xcan.angus.core.gm.interfaces.client.facade.internal.assembler.ClientSignAssembler.signup2Vo;

import cloud.xcan.angus.api.commonlink.client.ClientAuth;
import cloud.xcan.angus.api.gm.client.dto.ClientSignInDto;
import cloud.xcan.angus.api.gm.client.dto.ClientSignupDto;
import cloud.xcan.angus.api.gm.client.vo.ClientSignVo;
import cloud.xcan.angus.api.gm.client.vo.ClientSignupVo;
import cloud.xcan.angus.core.gm.application.cmd.client.ClientSignCmd;
import cloud.xcan.angus.core.gm.interfaces.client.facade.ClientSignFacade;
import jakarta.annotation.Resource;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ClientSignFacadeImpl implements ClientSignFacade {

  @Resource
  private ClientSignCmd clientSignCmd;

  @Override
  public ClientSignVo signin(ClientSignInDto dto) {
    Map<String, String> result = clientSignCmd.signin(dto.getClientId(),
        dto.getClientSecret(), dto.getScope());
    return signInToVo(result);
  }

  @Override
  public ClientSignupVo signupByDoor(ClientSignupDto dto) {
    ClientAuth clientAuth = clientSignCmd.signupByDoor(dto.getSignupBiz(), dto.getTenantId(),
        dto.getTenantName(), dto.getResourceId());
    return signup2Vo(clientAuth);
  }

}
