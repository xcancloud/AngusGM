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

@Tag(name = "Auth Client", description = "Manages OAuth2 client registrations and configurations. "
    + "OAuth2 clients act on behalf of users to request resource access permissions "
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

  @Operation(summary = "Create new OAuth2 client", operationId = "client:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "OAuth2 client created successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<IdKey<String, Object>> add(@Valid @RequestBody AuthClientAddDto dto) {
    return ApiLocaleResult.success(authClientFacade.add(dto));
  }

  @Operation(summary = "Update OAuth2 client configuration", operationId = "client:update")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Client configuration updated successfully"),
      @ApiResponse(responseCode = "404", description = "Client not found")
  })
  @PatchMapping
  public ApiLocaleResult<?> update(@Valid @RequestBody AuthClientUpdateDto dto) {
    authClientFacade.update(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Replace OAuth2 client configuration", operationId = "client:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Client configuration replaced successfully"),
      @ApiResponse(responseCode = "404", description = "Client not found")
  })
  @PutMapping
  public ApiLocaleResult<IdKey<String, Object>> replace(@Valid @RequestBody AuthClientReplaceDto dto) {
    return ApiLocaleResult.success(authClientFacade.replace(dto));
  }

  @Operation(summary = "Delete OAuth2 clients", operationId = "client:delete")
  @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Clients deleted successfully")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping
  public void delete(
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @RequestParam("ids") HashSet<String> clientIds) {
    authClientFacade.delete(clientIds);
  }

  @Operation(summary = "Retrieve OAuth2 client details", operationId = "client:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Client details retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Client not found")})
  @GetMapping(value = "/{id}")
  public ApiLocaleResult<AuthClientDetailVo> detail(
      @Parameter(name = "id", description = "OAuth2 client identifier", required = true) @PathVariable("id") String id) {
    return ApiLocaleResult.success(authClientFacade.detail(id));
  }

  @Operation(summary = "Retrieve OAuth2 client list", operationId = "client:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Client list retrieved successfully")})
  @GetMapping
  public ApiLocaleResult<List<AuthClientDetailVo>> list(@Valid AuthClientFindDto dto) {
    return ApiLocaleResult.success(authClientFacade.list(dto));
  }

}
