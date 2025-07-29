package cloud.xcan.angus.core.gm.interfaces.auth;

import cloud.xcan.angus.api.gm.client.dto.AuthClientUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.auth.facade.AuthClientFacade;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth Client Internal", description = "Internal API for OAuth2 client management operations")
@PreAuthorize("hasAuthority('SCOPE_inner_api_trust')")
@Validated
@RestController
@RequestMapping("/innerapi/v1/auth/client")
public class AuthClientDoorRest {

  @Resource
  private AuthClientFacade authClientFacade;

  @Operation(summary = "Update OAuth2 client configuration", operationId = "client:update:inner")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Client configuration updated successfully"),
      @ApiResponse(responseCode = "404", description = "Client not found")
  })
  @ResponseStatus(HttpStatus.OK)
  @PatchMapping
  public ApiLocaleResult<?> update(@Valid @RequestBody AuthClientUpdateDto dto) {
    authClientFacade.update(dto);
    return ApiLocaleResult.success();
  }

}
