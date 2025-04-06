package cloud.xcan.angus.core.gm.interfaces.client;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;

import cloud.xcan.angus.api.gm.client.dto.ClientAddDto;
import cloud.xcan.angus.api.gm.client.dto.ClientReplaceDto;
import cloud.xcan.angus.api.gm.client.dto.ClientUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.client.facade.ClientFacade;
import cloud.xcan.angus.core.gm.interfaces.client.facade.dto.ClientFindDto;
import cloud.xcan.angus.core.gm.interfaces.client.facade.vo.ClientDetailVo;
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

@Tag(name = "Client", description = "Manages OAuth2 client registrations. "
    + "The OAuth2 client acts on behalf of the user to request resource access permissions "
    + "and securely exchange credentials for access tokens to interact with protected APIs.")
@OperationClient
@PreAuthorize("@PPS.isOpClient()")
@Conditional(value = CloudServiceEditionCondition.class)
@Validated
@RestController
@RequestMapping("/api/v1/client")
public class ClientRest {

  @Resource
  private ClientFacade clientFacade;

  @Operation(description = "Add oauth2 registered client.", operationId = "client:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<IdKey<String, Object>> add(@Valid @RequestBody ClientAddDto dto) {
    return ApiLocaleResult.success(clientFacade.add(dto));
  }

  @Operation(description = "Update oauth2 registered client.", operationId = "client:update")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Updated successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")
  })
  @PatchMapping
  public ApiLocaleResult<?> update(@Valid @RequestBody ClientUpdateDto dto) {
    clientFacade.update(dto);
    return ApiLocaleResult.success();
  }

  @Operation(description = "Replace oauth2 registered client.", operationId = "client:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")
  })
  @PutMapping
  public ApiLocaleResult<IdKey<String, Object>> replace(@Valid @RequestBody ClientReplaceDto dto) {
    return ApiLocaleResult.success(clientFacade.replace(dto));
  }

  @Operation(description = "Delete oauth2 registered clients.", operationId = "client:delete")
  @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Deleted successfully")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping
  public void delete(
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @RequestParam("ids") HashSet<String> clientIds) {
    clientFacade.delete(clientIds);
  }

  @Operation(description = "Query the detail of oauth2 registered client.", operationId = "client:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping(value = "/{id}")
  public ApiLocaleResult<ClientDetailVo> detail(
      @Parameter(name = "id", description = "Client id", required = true) @PathVariable("id") String id) {
    return ApiLocaleResult.success(clientFacade.detail(id));
  }

  @Operation(description = "Query the list of oauth2 registered client.", operationId = "client:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping
  public ApiLocaleResult<List<ClientDetailVo>> list(@Valid ClientFindDto dto) {
    return ApiLocaleResult.success(clientFacade.list(dto));
  }

}
