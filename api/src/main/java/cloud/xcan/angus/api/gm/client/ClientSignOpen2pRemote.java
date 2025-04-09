package cloud.xcan.angus.api.gm.client;

import cloud.xcan.angus.api.gm.client.dto.ClientSigninDto;
import cloud.xcan.angus.api.gm.client.vo.ClientSignVo;
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


@FeignClient(name = "ANGUSGM", url = "${xcan.cloud.gmApiUrlPrefix}")
public interface ClientSignOpen2pRemote {

  @Operation(description = "Client sign-in for private and 3rd authorization.", operationId = "client:signin:pub")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Signin successfully")})
  @PostMapping(value = "/pubapi/v1/client/signin")
  ApiLocaleResult<ClientSignVo> signin(@Valid @RequestBody ClientSigninDto dto);


}
