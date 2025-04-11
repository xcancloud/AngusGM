package cloud.xcan.angus.core.gm.interfaces.client.facade;

import cloud.xcan.angus.api.gm.client.dto.ClientSigninDto;
import cloud.xcan.angus.api.gm.client.dto.ClientSignupDto;
import cloud.xcan.angus.api.gm.client.vo.ClientSignVo;
import cloud.xcan.angus.api.gm.client.vo.ClientSignupVo;

public interface ClientSignFacade {

  ClientSignVo signin(ClientSigninDto dto);

  ClientSignupVo signupByDoor(ClientSignupDto dto);
}
