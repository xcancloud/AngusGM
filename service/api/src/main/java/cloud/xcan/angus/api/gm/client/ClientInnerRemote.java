package cloud.xcan.angus.api.gm.client;

import cloud.xcan.angus.api.gm.client.dto.ClientUpdateDto;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "${xcan.service.gm:XCAN-ANGUSGM.BOOT}")
public interface ClientInnerRemote {

  @Operation(summary = "Update oauth2 registered client.", operationId = "client:update:inner")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Updated successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")
  })
  @PatchMapping(value = "/innerapi/v1/client")
  ApiLocaleResult<?> update(@RequestBody ClientUpdateDto dto);
}
