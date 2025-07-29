package cloud.xcan.angus.core.gm.interfaces.policy;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;

import cloud.xcan.angus.core.gm.interfaces.policy.facade.AuthPolicyFacade;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.AuthPolicyAddDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.AuthPolicyFindDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.AuthPolicyInitDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.AuthPolicyReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.AuthPolicyUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.AuthPolicyDetailVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.AuthPolicyVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.remote.dto.EnabledOrDisabledDto;
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
import org.springdoc.core.annotations.ParameterObject;
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


@Tag(name = "Auth Policy", description = "REST API endpoints for managing authorization policies and their configurations")
@Validated
@RestController
@RequestMapping("/api/v1/auth/policy")
public class AuthPolicyRest {

  @Resource
  private AuthPolicyFacade authPolicyFacade;

  @Operation(summary = "Create multiple authorization policies", operationId = "auth:policy:add")
  @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Authorization policies created successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<List<IdKey<Long, Object>>> add(
      @Valid @Size(max = MAX_BATCH_SIZE) @RequestBody List<AuthPolicyAddDto> dto) {
    return ApiLocaleResult.success(authPolicyFacade.add(dto));
  }

  @Operation(summary = "Update existing authorization policies", operationId = "auth:policy:update")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Authorization policies updated successfully"),
      @ApiResponse(responseCode = "404", description = "One or more policies not found")
  })
  @PatchMapping
  public ApiLocaleResult<?> update(
      @Valid @Size(max = MAX_BATCH_SIZE) @RequestBody List<AuthPolicyUpdateDto> dto) {
    authPolicyFacade.update(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Create or replace authorization policies", operationId = "auth:policy:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Authorization policies created or replaced successfully"),
      @ApiResponse(responseCode = "404", description = "One or more policies not found")
  })
  @PutMapping
  public ApiLocaleResult<List<IdKey<Long, Object>>> replace(
      @Valid @Size(max = MAX_BATCH_SIZE) @RequestBody List<AuthPolicyReplaceDto> dto) {
    return ApiLocaleResult.success(authPolicyFacade.replace(dto));
  }

  @OperationClient
  @PreAuthorize("@PPS.isOpClient()")
  @Operation(summary = "Initialize authorization policies for tenant or platform users", operationId = "auth:policy:init")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Authorization policies initialized successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/init")
  public ApiLocaleResult<?> init(@Valid @RequestBody AuthPolicyInitDto dto) {
    authPolicyFacade.init(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Enable or disable authorization policies", operationId = "auth:policy:enabled")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Authorization policies enabled or disabled successfully"),
      @ApiResponse(responseCode = "404", description = "One or more policies not found")
  })
  @PatchMapping("/enabled")
  public ApiLocaleResult<?> enabled(
      @Valid @Size(max = MAX_BATCH_SIZE) @RequestBody List<EnabledOrDisabledDto> dto) {
    authPolicyFacade.enabled(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Delete multiple authorization policies", operationId = "auth:policy:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Authorization policies deleted successfully")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping
  public void delete(
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @RequestParam("ids") HashSet<Long> ids) {
    authPolicyFacade.delete(ids);
  }

  @Operation(summary = "Retrieve detailed information about an authorization policy", operationId = "auth:policy:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Authorization policy details retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Policy not found")})
  @GetMapping(value = "/{idOrCode}")
  public ApiLocaleResult<AuthPolicyDetailVo> detail(
      @Parameter(name = "idOrCode", description = "Authorization policy identifier or code", required = true)
      @PathVariable("idOrCode") String idOrCode) {
    return ApiLocaleResult.success(authPolicyFacade.detail(idOrCode));
  }

  @Operation(summary = "Search and retrieve authorization policies with pagination", operationId = "auth:policy:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Authorization policy list retrieved successfully")})
  @GetMapping
  public ApiLocaleResult<PageResult<AuthPolicyVo>> list(@Valid @ParameterObject AuthPolicyFindDto dto) {
    return ApiLocaleResult.success(authPolicyFacade.list(dto));
  }

}
