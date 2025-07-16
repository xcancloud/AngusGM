package cloud.xcan.angus.core.gm.interfaces.auth;

import cloud.xcan.angus.api.gm.client.dto.AuthClientSignInDto;
import cloud.xcan.angus.api.gm.client.vo.AuthClientSignVo;
import cloud.xcan.angus.core.gm.interfaces.auth.facade.AuthClientSignFacade;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AuthClientSignPub", description = "Used for OAuth2 client login to obtain inter system call access token")
@Validated
@RestController
@RequestMapping("/pubapi/v1/auth/client")
public class AuthClientSignPubRest {

  @Resource
  private AuthClientSignFacade authClientSignFacade;

  @Operation(summary = "Authorization client sign-in",
      description = "Client sign-in for private, 3rd authorization or inner application", operationId = "client:signin:pub")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Sign-in successfully")})
  @PostMapping(value = "/signin")
  public ApiLocaleResult<AuthClientSignVo> signin(@Valid @RequestBody AuthClientSignInDto dto) {
    return ApiLocaleResult.success(authClientSignFacade.signin(dto));
  }

}
