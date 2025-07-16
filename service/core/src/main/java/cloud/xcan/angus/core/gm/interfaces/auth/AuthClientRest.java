package cloud.xcan.angus.core.gm.interfaces.auth;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;

import cloud.xcan.angus.api.gm.client.dto.AuthClientAddDto;
import cloud.xcan.angus.api.gm.client.dto.AuthClientReplaceDto;
import cloud.xcan.angus.api.gm.client.dto.AuthClientUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.auth.facade.AuthClientFacade;
import cloud.xcan.angus.core.gm.interfaces.auth.facade.dto.AuthClientFindDto;
import cloud.xcan.angus.core.gm.interfaces.auth.facade.vo.AuthClientDetailVo;
import cloud.xcan.angus.core.spring.condition.CloudServiceEditionCondition;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.spec.annotations.OperationClient;
import cloud.xcan.angus.spec.experimental.IdKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AuthClient", description = "Manages OAuth2 client registrations. "
    + "The OAuth2 client acts on behalf of the user to request resource access permissions "
    + "and securely exchange credentials for access tokens to interact with protected APIs")
@OperationClient
@PreAuthorize("@PPS.isOpClient()")
@Conditional(value = CloudServiceEditionCondition.class)
@Validated
@RestController
@RequestMapping("/api/v1/auth/client")
public class AuthClientRest {

  @Resource
  private AuthClientFacade authClientFacade;

  @Operation(summary = "Add oauth2 registered client", operationId = "client:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<IdKey<String, Object>> add(@Valid @RequestBody AuthClientAddDto dto) {
    return ApiLocaleResult.success(authClientFacade.add(dto));
  }

  @Operation(summary = "Update oauth2 registered client", operationId = "client:update")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Updated successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")
  })
  @PatchMapping
  public ApiLocaleResult<?> update(@Valid @RequestBody AuthClientUpdateDto dto) {
    authClientFacade.update(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Replace oauth2 registered client", operationId = "client:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")
  })
  @PutMapping
  public ApiLocaleResult<IdKey<String, Object>> replace(@Valid @RequestBody AuthClientReplaceDto dto) {
    return ApiLocaleResult.success(authClientFacade.replace(dto));
  }

  @Operation(summary = "Delete oauth2 registered clients", operationId = "client:delete")
  @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Deleted successfully")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping
  public void delete(
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @RequestParam("ids") HashSet<String> clientIds) {
    authClientFacade.delete(clientIds);
  }

  @Operation(summary = "Query the detail of oauth2 registered client", operationId = "client:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping(value = "/{id}")
  public ApiLocaleResult<AuthClientDetailVo> detail(
      @Parameter(name = "id", description = "Client id", required = true) @PathVariable("id") String id) {
    return ApiLocaleResult.success(authClientFacade.detail(id));
  }

  @Operation(summary = "Query the list of oauth2 registered client", operationId = "client:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping
  public ApiLocaleResult<List<AuthClientDetailVo>> list(@Valid AuthClientFindDto dto) {
    return ApiLocaleResult.success(authClientFacade.list(dto));
  }

}
