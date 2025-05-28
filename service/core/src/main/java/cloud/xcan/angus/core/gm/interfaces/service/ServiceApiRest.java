package cloud.xcan.angus.core.gm.interfaces.service;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;

import cloud.xcan.angus.core.gm.interfaces.service.facade.ServiceApiFacade;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.ServiceApiAddDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceApiVo;
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
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "ServiceApi", description = "Provides a unified entry for managing Angus series application services and associated apis.")
@Validated
@RestController
@RequestMapping("/api/v1/service")
public class ServiceApiRest {

  @Resource
  private ServiceApiFacade serviceApiFacade;

  @OperationClient
  @PreAuthorize("@PPS.isOpClient()")
  @Operation(summary = "Add the apis to service.", operationId = "service:api:add")
  @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Created successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/api")
  public ApiLocaleResult<List<IdKey<Long, Object>>> apiAdd(
      @Valid @Size(max = MAX_BATCH_SIZE) @RequestBody List<ServiceApiAddDto> dto) {
    return ApiLocaleResult.success(serviceApiFacade.apiAdd(dto));
  }

  @OperationClient
  @PreAuthorize("@PPS.isOpClient()")
  @Operation(summary = "Synchronize the swagger apis from AngusDiscovery specified service instance.",
      operationId = "service:api:discovery:sync")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Synchronize successfully")})
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/{serviceCode}/api/discovery/sync")
  public ApiLocaleResult<?> syncServiceApi(
      @Parameter(name = "serviceCode", description = "Service Code", required = true) @PathVariable("serviceCode") String serviceCode) {
    serviceApiFacade.syncServiceApi(serviceCode);
    return ApiLocaleResult.success();
  }

  @OperationClient
  @PreAuthorize("@PPS.isOpClient()")
  @Operation(summary = "Synchronize the all swagger apis from AngusDiscovery service instances.",
      operationId = "service:api:discovery:syncs")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Created successfully")})
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/api/discovery/sync")
  public ApiLocaleResult<?> discoveryApiSync() {
    serviceApiFacade.discoveryApiSync();
    return ApiLocaleResult.success();
  }

  @OperationClient
  @PreAuthorize("@PPS.isOpClient()")
  @Operation(summary = "Delete the apis of service.", operationId = "service:api:delete")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Deleted successfully")})
  @DeleteMapping("/{id}/api")
  public void apiDelete(
      @Parameter(name = "id", description = "Service id", required = true) @PathVariable("id") Long id,
      @Valid @RequestParam("ids") @Size(max = MAX_BATCH_SIZE) HashSet<Long> ids) {
    serviceApiFacade.apiDelete(id, ids);
  }

  @Operation(summary = "Query the list of service.", operationId = "service:api:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/{id}/api")
  public ApiLocaleResult<List<ServiceApiVo>> apiList(
      @Parameter(name = "id", description = "Service id", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(serviceApiFacade.apiList(id));
  }

}
