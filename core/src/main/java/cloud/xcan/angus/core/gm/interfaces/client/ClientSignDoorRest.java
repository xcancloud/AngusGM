package cloud.xcan.angus.core.gm.interfaces.client;

import cloud.xcan.angus.api.gm.client.dto.ClientSignupDto;
import cloud.xcan.angus.api.gm.client.vo.ClientSignupVo;
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

@Tag(name = "ClientSignDoor", description = "Used for OAuth2 client registration.")
@Validated
@RestController
@RequestMapping("/innerapi/v1/client")
public class ClientSignDoorRest {

  @Resource
  private ClientSignFacade clientSignFacade;

  @Operation(description = "Signup oauth2 client for private application edition or agent.", operationId = "client:signup:door")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Signup successfully")})
  @PostMapping(value = "/signup")
  public ApiLocaleResult<ClientSignupVo> signupByDoor(@Valid @RequestBody ClientSignupDto dto) {
    return ApiLocaleResult.success(clientSignFacade.signupByDoor(dto));
  }

}
