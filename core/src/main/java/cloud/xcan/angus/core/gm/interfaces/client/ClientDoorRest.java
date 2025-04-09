package cloud.xcan.angus.core.gm.interfaces.client;

import cloud.xcan.angus.api.gm.client.dto.ClientUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.client.facade.ClientFacade;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "ClientDoor", description = "Provides client entry for internal system updates.")
@Validated
@RestController
@RequestMapping("/innerapi/v1/client")
public class ClientDoorRest {

  @Resource
  private ClientFacade clientFacade;

  @Operation(description = "Update oauth2 registered client.", operationId = "client:update:inner")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Updated successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")
  })
  @PatchMapping
  public ApiLocaleResult<?> update(@Valid @RequestBody ClientUpdateDto dto) {
    clientFacade.update(dto);
    return ApiLocaleResult.success();
  }

}
