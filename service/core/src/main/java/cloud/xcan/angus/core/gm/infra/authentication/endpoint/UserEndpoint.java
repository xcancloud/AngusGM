package cloud.xcan.angus.core.gm.infra.authentication.endpoint;

import static cloud.xcan.angus.api.commonlink.AuthConstant.TOKEN_REVOKE_ENDPOINT;
import static cloud.xcan.angus.api.commonlink.AuthConstant.USER_INFO_ENDPOINT;
import static java.util.Objects.nonNull;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "Unified user management entry for the system.")
@RestController
public class UserEndpoint {

  @Resource
  private OAuth2AuthorizationService oauth2AuthorizationService;

  @ResponseBody
  @GetMapping(USER_INFO_ENDPOINT)
  public Principal user(Principal principal) {
    return principal;
  }

  @ResponseBody
  @Operation(summary = "Revoke access token", operationId = "auth:token:revoke")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Revoke token successfully")})
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
