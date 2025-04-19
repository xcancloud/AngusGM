package cloud.xcan.angus.core.gm.interfaces.client.facade;

import cloud.xcan.angus.api.gm.client.dto.ClientSignInDto;
import cloud.xcan.angus.api.gm.client.dto.ClientSignupDto;
import cloud.xcan.angus.api.gm.client.vo.ClientSignVo;
import cloud.xcan.angus.api.gm.client.vo.ClientSignupVo;

public interface ClientSignFacade {

  ClientSignVo signin(ClientSignInDto dto);

  ClientSignupVo signupByDoor(ClientSignupDto dto);
}
