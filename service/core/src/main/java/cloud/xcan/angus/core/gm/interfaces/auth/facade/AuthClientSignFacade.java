package cloud.xcan.angus.core.gm.interfaces.auth.facade;

import cloud.xcan.angus.api.gm.client.dto.AuthClientSignInDto;
import cloud.xcan.angus.api.gm.client.dto.AuthClientSignupDto;
import cloud.xcan.angus.api.gm.client.vo.AuthClientSignVo;
import cloud.xcan.angus.api.gm.client.vo.AuthClientSignupVo;

public interface AuthClientSignFacade {

  AuthClientSignVo signin(AuthClientSignInDto dto);

  AuthClientSignupVo signupByDoor(AuthClientSignupDto dto);
}
