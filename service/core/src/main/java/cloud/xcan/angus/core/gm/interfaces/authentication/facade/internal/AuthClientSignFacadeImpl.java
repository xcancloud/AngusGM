package cloud.xcan.angus.core.gm.interfaces.authentication.facade.internal;


import static cloud.xcan.angus.core.gm.interfaces.authentication.facade.internal.assembler.AuthClientSignAssembler.signInToVo;
import static cloud.xcan.angus.core.gm.interfaces.authentication.facade.internal.assembler.AuthClientSignAssembler.signup2Vo;

import cloud.xcan.angus.api.commonlink.client.ClientAuth;
import cloud.xcan.angus.api.gm.client.dto.AuthClientSignInDto;
import cloud.xcan.angus.api.gm.client.dto.AuthClientSignupDto;
import cloud.xcan.angus.api.gm.client.vo.AuthClientSignVo;
import cloud.xcan.angus.api.gm.client.vo.AuthClientSignupVo;
import cloud.xcan.angus.core.gm.application.cmd.authentication.AuthClientSignCmd;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.AuthClientSignFacade;
import jakarta.annotation.Resource;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class AuthClientSignFacadeImpl implements AuthClientSignFacade {

  @Resource
  private AuthClientSignCmd authClientSignCmd;

  @Override
  public AuthClientSignVo signin(AuthClientSignInDto dto) {
    Map<String, String> result = authClientSignCmd.signin(dto.getClientId(),
        dto.getClientSecret(), dto.getScope());
    return signInToVo(result);
  }

  @Override
  public AuthClientSignupVo signupByDoor(AuthClientSignupDto dto) {
    ClientAuth clientAuth = authClientSignCmd.signupByDoor(dto.getSignupBiz(), dto.getTenantId(),
        dto.getTenantName(), dto.getResourceId());
    return signup2Vo(clientAuth);
  }

}
