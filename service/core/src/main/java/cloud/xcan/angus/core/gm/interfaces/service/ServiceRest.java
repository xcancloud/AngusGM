package cloud.xcan.angus.core.gm.interfaces.service;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;

import cloud.xcan.angus.core.gm.interfaces.service.facade.ServiceFacade;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.ServiceAddDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.ServiceFindDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.ServiceReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.ServiceSearchDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.ServiceUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ResourceApiVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceResourceVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceVo;
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
import org.hibernate.validator.constraints.Length;
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


@Tag(name = "Service", description = "Provides a unified entry for managing Angus series application services.")
@Validated
@RestController
@RequestMapping("/api/v1/service")
public class ServiceRest {

  @Resource
  private ServiceFacade serviceFacade;

  @OperationClient
  @PreAuthorize("@PPS.isOpClient()")
  @Operation(summary = "Add services.", operationId = "service:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<IdKey<Long, Object>> add(@Valid @RequestBody ServiceAddDto dto) {
    return ApiLocaleResult.success(serviceFacade.add(dto));
  }

  @OperationClient
  @PreAuthorize("@PPS.isOpClient()")
  @Operation(summary = "Update services.", operationId = "service:update")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Updated successfully")})
  @PatchMapping
  public ApiLocaleResult<?> update(@Valid @RequestBody ServiceUpdateDto dto) {
    serviceFacade.update(dto);
    return ApiLocaleResult.success();
  }

  @OperationClient
  @PreAuthorize("@PPS.isOpClient()")
  @Operation(summary = "Replace services.", operationId = "service:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully")})
  @PutMapping
  public ApiLocaleResult<IdKey<Long, Object>> replace(@Valid @RequestBody ServiceReplaceDto dto) {
    return ApiLocaleResult.success(serviceFacade.replace(dto));
  }

  @OperationClient
  @PreAuthorize("@PPS.isOpClient()")
  @Operation(summary = "Delete services.", operationId = "service:delete")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Deleted successfully")})
  @DeleteMapping
  public void delete(
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @RequestParam("ids") HashSet<Long> ids) {
    serviceFacade.delete(ids);
  }

  @OperationClient
  @PreAuthorize("@PPS.isOpClient()")
  @Operation(summary = "Enable or disable services.", operationId = "service:enabled")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Enabled or disabled successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")
  })
  @PatchMapping("/enabled")
  public ApiLocaleResult<?> enabled(
      @Valid @Size(max = MAX_BATCH_SIZE) @RequestBody List<EnabledOrDisabledDto> dto) {
    serviceFacade.enabled(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Query the detail of service.", operationId = "service:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping(value = "/{id}")
  public ApiLocaleResult<ServiceVo> detail(
      @Parameter(name = "id", description = "Service id", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(serviceFacade.detail(id));
  }

  @Operation(summary = "Query the list of service.", operationId = "service:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping
  public ApiLocaleResult<PageResult<ServiceVo>> list(@Valid @ParameterObject ServiceFindDto dto) {
    return ApiLocaleResult.success(serviceFacade.list(dto));
  }

  @Operation(summary = "Fulltext search the list of service.", operationId = "service:search")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/search")
  public ApiLocaleResult<PageResult<ServiceVo>> search(@Valid @ParameterObject ServiceSearchDto dto) {
    return ApiLocaleResult.success(serviceFacade.search(dto));
  }

  @Operation(summary = "Query the all resources of service.", operationId = "service:resource:all")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")
  })
  @GetMapping(value = "/resource")
  public ApiLocaleResult<List<ServiceResourceVo>> resourceList(
      @Valid @Length(max = MAX_CODE_LENGTH) @Parameter(name = "serviceCode", description = "Service code", required = false)
      @RequestParam(value = "serviceCode", required = false) String serviceCode,
      @RequestParam(value = "auth", required = false) Boolean auth) {
    return ApiLocaleResult.success(serviceFacade.resourceList(serviceCode, auth));
  }

  @Operation(summary = "Query the all apis of service or resource.", operationId = "service:resource:api:all")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping(value = "/resource/api")
  public ApiLocaleResult<List<ResourceApiVo>> resourceApiList(
      @Valid @Length(max = MAX_CODE_LENGTH) @Parameter(name = "serviceCode", description = "Service code", required = true)
      @RequestParam(value = "serviceCode", required = true) String serviceCode,
      @Valid @Length(max = MAX_NAME_LENGTH) @Parameter(name = "resourceName", description = "Resource name", required = false)
      @RequestParam(value = "resourceName", required = false) String resourceName,
      @RequestParam(value = "auth", required = false) Boolean auth) {
    return ApiLocaleResult.success(serviceFacade.resourceApiList(serviceCode, resourceName, auth));
  }
}
