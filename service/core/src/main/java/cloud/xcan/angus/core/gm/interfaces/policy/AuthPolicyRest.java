package cloud.xcan.angus.core.gm.interfaces.policy;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;

import cloud.xcan.angus.core.gm.interfaces.policy.facade.AuthPolicyFacade;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.AuthPolicyAddDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.AuthPolicyFindDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.AuthPolicyInitDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.AuthPolicyReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.AuthPolicySearchDto;
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


@Tag(name = "AuthPolicy", description =
    "Provides a unified entry for authorization policies management. "
        + "Authorization policies control access to resources and define permissions for users and applications")
@Validated
@RestController
@RequestMapping("/api/v1/auth/policy")
public class AuthPolicyRest {

  @Resource
  private AuthPolicyFacade authPolicyFacade;

  @Operation(summary = "Add authorization policies", operationId = "auth:policy:add")
  @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Created successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<List<IdKey<Long, Object>>> add(
      @Valid @Size(max = MAX_BATCH_SIZE) @RequestBody List<AuthPolicyAddDto> dto) {
    return ApiLocaleResult.success(authPolicyFacade.add(dto));
  }

  @Operation(summary = "Update authorization policies", operationId = "auth:policy:update")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Updated successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")
  })
  @PatchMapping
  public ApiLocaleResult<?> update(
      @Valid @Size(max = MAX_BATCH_SIZE) @RequestBody List<AuthPolicyUpdateDto> dto) {
    authPolicyFacade.update(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Replace authorization policies", operationId = "auth:policy:update")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")
  })
  @PutMapping
  public ApiLocaleResult<List<IdKey<Long, Object>>> replace(
      @Valid @Size(max = MAX_BATCH_SIZE) @RequestBody List<AuthPolicyReplaceDto> dto) {
    return ApiLocaleResult.success(authPolicyFacade.replace(dto));
  }

  @OperationClient
  @PreAuthorize("@PPS.isOpClient()")
  @Operation(summary = "Initialize the authorization policy for tenant or platform users", operationId = "auth:policy:init")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Initialized successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/init")
  public ApiLocaleResult<?> init(@Valid @RequestBody AuthPolicyInitDto dto) {
    authPolicyFacade.init(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Enabled or disabled the authorization policies", operationId = "auth:policy:enabled")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Updated successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")
  })
  @PatchMapping("/enabled")
  public ApiLocaleResult<?> enabled(
      @Valid @Size(max = MAX_BATCH_SIZE) @RequestBody List<EnabledOrDisabledDto> dto) {
    authPolicyFacade.enabled(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Delete authorization policies", operationId = "auth:policy:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Deleted successfully")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping
  public void delete(
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @RequestParam("ids") HashSet<Long> ids) {
    authPolicyFacade.delete(ids);
  }

  @Operation(summary = "Query the detail of authorization policy", operationId = "auth:policy:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping(value = "/{idOrCode}")
  public ApiLocaleResult<AuthPolicyDetailVo> detail(
      @Parameter(name = "idOrCode", description = "Authorization policy id or code", required = true)
      @PathVariable("idOrCode") String idOrCode) {
    return ApiLocaleResult.success(authPolicyFacade.detail(idOrCode));
  }

  @Operation(summary = "Query the list of authorization policy", operationId = "auth:policy:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping
  public ApiLocaleResult<PageResult<AuthPolicyVo>> list(@Valid @ParameterObject AuthPolicyFindDto dto) {
    return ApiLocaleResult.success(authPolicyFacade.list(dto));
  }

  @Operation(summary = "Fulltext search the list of authorization policy", operationId = "auth:policy:search")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/search")
  public ApiLocaleResult<PageResult<AuthPolicyVo>> search(@Valid @ParameterObject AuthPolicySearchDto dto) {
    return ApiLocaleResult.success(authPolicyFacade.search(dto));
  }

}
