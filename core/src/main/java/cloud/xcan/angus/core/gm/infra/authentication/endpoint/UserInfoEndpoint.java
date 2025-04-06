package cloud.xcan.angus.core.gm.infra.authentication.endpoint;

import static cloud.xcan.angus.api.commonlink.AASConstant.USER_INFO_ENDPOINT;

import java.security.Principal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserInfoEndpoint {

  @ResponseBody
  @GetMapping(USER_INFO_ENDPOINT)
  public Principal user(Principal principal) {
    return principal;
  }

}
