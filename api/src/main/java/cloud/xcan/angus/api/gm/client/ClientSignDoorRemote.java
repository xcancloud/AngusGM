package cloud.xcan.angus.api.gm.client;

import cloud.xcan.angus.api.gm.client.dto.ClientSignupDto;
import cloud.xcan.angus.api.gm.client.vo.ClientSignupVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;


@FeignClient(name = "${xcan.service.gm:ANGUSGM}")
public interface ClientSignDoorRemote {

  @Operation(description = "Signup oauth2 client for private application edition or agent.", operationId = "client:signup:inner")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Signup successfully")})
  @PostMapping(value = "/innerapi/v1/client/signup")
  ApiLocaleResult<ClientSignupVo> signupByDoor(@Valid @RequestBody ClientSignupDto dto);

}
