package cloud.xcan.angus.core.gm.infra.authentication.endpoint;


import static cloud.xcan.angus.api.commonlink.AASConstant.TOKEN_REVOKE_ENDPOINT;
import static java.util.Objects.nonNull;

import jakarta.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenRevokeEndpoint {

  @Resource
  private OAuth2AuthorizationService oauth2AuthorizationService;

  @ResponseBody
  @RequestMapping(method = RequestMethod.DELETE, value = TOKEN_REVOKE_ENDPOINT)
  public ResponseEntity<Map<String, ?>> revokeToken(String access_token) {
    OAuth2Authorization authorization = oauth2AuthorizationService.findByToken(access_token, null);
    if (nonNull(authorization)) {
      oauth2AuthorizationService.remove(authorization);
      return ResponseEntity.ok(Collections.emptyMap());
    }

    Map<String, String> result = new HashMap<>();
    result.put("error", "revoke_fail");
    result.put("error_description", "Token has been invalid or wrong");
    return ResponseEntity.badRequest().body(result);
  }
}
