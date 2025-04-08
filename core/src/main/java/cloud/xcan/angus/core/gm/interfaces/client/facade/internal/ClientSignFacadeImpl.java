package cloud.xcan.angus.core.gm.interfaces.client.facade.internal;


import static cloud.xcan.angus.core.gm.interfaces.client.facade.internal.assembler.ClientSignAssembler.signInToVo;
import static cloud.xcan.angus.core.gm.interfaces.client.facade.internal.assembler.ClientSignAssembler.signup2Vo;

import cloud.xcan.angus.api.commonlink.client.ClientAuth;
import cloud.xcan.angus.api.gm.client.dto.ClientSigninDto;
import cloud.xcan.angus.api.gm.client.dto.ClientSignupDto;
import cloud.xcan.angus.api.gm.client.vo.ClientSignVo;
import cloud.xcan.angus.api.gm.client.vo.ClientSignupVo;
import cloud.xcan.angus.core.gm.application.cmd.client.ClientSignCmd;
import cloud.xcan.angus.core.gm.interfaces.client.facade.ClientSignFacade;
import jakarta.annotation.Resource;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Component;

@Component
public class ClientSignFacadeImpl implements ClientSignFacade {

  @Resource
  private ClientSignCmd clientSignCmd;

  @Override
  public ClientSignVo signin(ClientSigninDto dto) {
    OAuth2AccessToken accessToken = clientSignCmd.signin(dto.getClientId(),
        dto.getClientSecret(), dto.getScope());
    return signInToVo(accessToken);
  }

  @Override
  public ClientSignupVo signupByDoor(ClientSignupDto dto) {
    ClientAuth clientAuth = clientSignCmd.signupByDoor(dto.getSignupBiz(), dto.getTenantId(),
        dto.getTenantName(), dto.getResourceId());
    return signup2Vo(clientAuth);
  }

}
