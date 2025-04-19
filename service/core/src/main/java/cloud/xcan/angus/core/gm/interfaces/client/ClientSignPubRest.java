package cloud.xcan.angus.core.gm.interfaces.client;

import cloud.xcan.angus.api.gm.client.dto.ClientSignInDto;
import cloud.xcan.angus.api.gm.client.vo.ClientSignVo;
import cloud.xcan.angus.core.gm.interfaces.client.facade.ClientSignFacade;
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

@Tag(name = "ClientSignPub", description = "Used for OAuth2 client login to obtain inter system call access token.")
@Validated
@RestController
@RequestMapping("/pubapi/v1/client")
public class ClientSignPubRest {

  @Resource
  private ClientSignFacade clientSignFacade;

  @Operation(description = "Client sign-in for private, 3rd authorization or inner application.", operationId = "client:signin:pub")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Sign-in successfully")})
  @PostMapping(value = "/signin")
  public ApiLocaleResult<ClientSignVo> signin(@Valid @RequestBody ClientSignInDto dto) {
    return ApiLocaleResult.success(clientSignFacade.signin(dto));
  }

}
