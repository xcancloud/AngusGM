package cloud.xcan.angus.core.gm.interfaces.api;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;
import static io.swagger.v3.oas.models.extension.ExtensionKey.RESOURCE_NAME_KEY;

import cloud.xcan.angus.core.gm.interfaces.api.facade.ApiFacade;
import cloud.xcan.angus.core.gm.interfaces.api.facade.dto.ApiAddDto;
import cloud.xcan.angus.core.gm.interfaces.api.facade.dto.ApiFindDto;
import cloud.xcan.angus.core.gm.interfaces.api.facade.dto.ApiReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.api.facade.dto.ApiSearchDto;
import cloud.xcan.angus.core.gm.interfaces.api.facade.dto.ApiUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.api.facade.vo.ApiDetailVo;
import cloud.xcan.angus.core.spring.condition.CloudServiceEditionCondition;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.remote.dto.EnabledOrDisabledDto;
import cloud.xcan.angus.spec.annotations.OperationClient;
import cloud.xcan.angus.spec.experimental.IdKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
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


@Tag(name = "Api", description = "Controls API endpoint definitions and access policies")
@Extension(properties = @ExtensionProperty(name = RESOURCE_NAME_KEY, value = "Api"))
@Conditional(CloudServiceEditionCondition.class)
@OperationClient
@Validated
@RestController
@RequestMapping("/api/v1/api")
public class ApiRest {

  @Resource
  private ApiFacade apiFacade;

  @OperationClient
  @PreAuthorize("@PPS.isOpClient()")
  @Operation(summary = "Add apis", operationId = "api:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<List<IdKey<Long, Object>>> add(
      @Valid @Size(max = MAX_BATCH_SIZE) @RequestBody List<ApiAddDto> dto) {
    return ApiLocaleResult.success(apiFacade.add(dto));
  }

  @OperationClient
  @PreAuthorize("@PPS.isOpClient()")
  @Operation(summary = "Update apis", operationId = "api:update")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Updated successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @PatchMapping
  public ApiLocaleResult<?> update(
      @Valid @Size(max = MAX_BATCH_SIZE) @RequestBody List<ApiUpdateDto> dto) {
    apiFacade.update(dto);
    return ApiLocaleResult.success();
  }

  @OperationClient
  @PreAuthorize("@PPS.isOpClient()")
  @Operation(summary = "Replace apis", operationId = "api:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully")})
  @PutMapping
  public ApiLocaleResult<List<IdKey<Long, Object>>> replace(
      @Valid @Size(max = MAX_BATCH_SIZE) @RequestBody List<ApiReplaceDto> dto) {
    return ApiLocaleResult.success(apiFacade.replace(dto));
  }

  @OperationClient
  @PreAuthorize("@PPS.isOpClient()")
  @Operation(summary = "Delete apis", operationId = "api:delete")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Deleted successfully")})
  @DeleteMapping
  public void delete(
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @Parameter(name = "ids", description = "Api ids", required = true)
      @RequestParam("ids") HashSet<Long> ids) {
    apiFacade.delete(ids);
  }

  @OperationClient
  @PreAuthorize("@PPS.isOpClient()")
  @Operation(summary = "Enable or disable apis", operationId = "api:enabled")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Enabled or disabled successfully")})
  @PatchMapping("/enabled")
  public ApiLocaleResult<?> enabled(
      @Valid @Size(max = MAX_BATCH_SIZE) @RequestBody List<EnabledOrDisabledDto> dtos) {
    apiFacade.enabled(dtos);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Query the detail of api", operationId = "api:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping(value = "/{id}")
  public ApiLocaleResult<ApiDetailVo> detail(
      @Parameter(name = "id", description = "Api id", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(apiFacade.detail(id));
  }

  @Operation(summary = "Query the list of api", operationId = "api:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping
  public ApiLocaleResult<PageResult<ApiDetailVo>> list(@Valid @ParameterObject ApiFindDto dto) {
    return ApiLocaleResult.success(apiFacade.list(dto));
  }

  @Operation(summary = "Fulltext search the list of api", operationId = "api:search")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/search")
  public ApiLocaleResult<PageResult<ApiDetailVo>> search(@Valid @ParameterObject ApiSearchDto dto) {
    return ApiLocaleResult.success(apiFacade.search(dto));
  }

}
