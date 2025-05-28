package cloud.xcan.angus.core.gm.interfaces.tenant;


import static cloud.xcan.angus.api.commonlink.UCConstant.TOP_TENANT_ADMIN;

import cloud.xcan.angus.api.gm.tenant.dto.TenantAddByMobileDto;
import cloud.xcan.angus.api.gm.tenant.dto.TenantAddDto;
import cloud.xcan.angus.api.gm.tenant.dto.TenantFindDto;
import cloud.xcan.angus.api.gm.tenant.dto.TenantLockedDto;
import cloud.xcan.angus.api.gm.tenant.dto.TenantReplaceDto;
import cloud.xcan.angus.api.gm.tenant.dto.TenantSearchDto;
import cloud.xcan.angus.api.gm.tenant.dto.TenantUpdateDto;
import cloud.xcan.angus.api.gm.tenant.vo.TenantDetailVo;
import cloud.xcan.angus.api.gm.tenant.vo.TenantVo;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.TenantFacade;
import cloud.xcan.angus.core.spring.condition.CloudServiceEditionCondition;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.remote.dto.EnabledOrDisabledDto;
import cloud.xcan.angus.spec.experimental.IdKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "Tenant", description = "Unified tenant management. Centralizes tenant account controls, configurations, and initialize access permissions, etc.")
@Conditional(CloudServiceEditionCondition.class)
@Validated
@RestController
@RequestMapping("/api/v1/tenant")
public class TenantRest {

  @Resource
  private TenantFacade tenantFacade;

  @PreAuthorize("@PPS.hasToPolicy('" + TOP_TENANT_ADMIN + "')")
  @Operation(summary = "Add tenant.", operationId = "tenant:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<IdKey<Long, Object>> add(@Valid @RequestBody TenantAddDto dto) {
    return ApiLocaleResult.success(tenantFacade.add(dto));
  }

  @PreAuthorize("@PPS.hasToPolicy('" + TOP_TENANT_ADMIN + "')")
  @Operation(summary = "Add tenant through mobile signup.", operationId = "tenant:signupByMobile:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/signupByMobile")
  public ApiLocaleResult<IdKey<Long, Object>> signupByMobile(
      @Valid @RequestBody TenantAddByMobileDto dto) {
    return ApiLocaleResult.success(tenantFacade.signupByMobile(dto));
  }

  @PreAuthorize("@PPS.hasToPolicy('" + TOP_TENANT_ADMIN + "')")
  @Operation(summary = "Update tenant.", operationId = "tenant:update")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Updated successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")
  })
  @PatchMapping
  public ApiLocaleResult<?> update(@Valid @RequestBody TenantUpdateDto dto) {
    tenantFacade.update(dto);
    return ApiLocaleResult.success();
  }

  @PreAuthorize("@PPS.hasToPolicy('" + TOP_TENANT_ADMIN + "')")
  @Operation(summary = "Replace tenant.", operationId = "tenant:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")
  })
  @PutMapping
  public ApiLocaleResult<?> replace(@Valid @RequestBody TenantReplaceDto dto) {
    tenantFacade.replace(dto);
    return ApiLocaleResult.success();
  }

  @PreAuthorize("@PPS.hasToPolicy('" + TOP_TENANT_ADMIN + "')")
  @Operation(summary = "Enable or disable tenant.", operationId = "tenant:enabled")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Enabled or disabled successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")
  })
  @PatchMapping("/enabled")
  public ApiLocaleResult<?> enabled(@Valid @RequestBody EnabledOrDisabledDto dto) {
    tenantFacade.enabled(dto);
    return ApiLocaleResult.success();
  }

  @PreAuthorize("@PPS.hasToPolicy('" + TOP_TENANT_ADMIN + "')")
  @Operation(summary = "DistributedLock or unlock tenant.", operationId = "tenant:locked")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully locked"),
      @ApiResponse(responseCode = "404", description = "Resource not found")
  })
  @PatchMapping("/locked")
  public ApiLocaleResult<?> locked(@Valid @RequestBody TenantLockedDto dto) {
    tenantFacade.locked(dto);
    return ApiLocaleResult.success();
  }

  @PreAuthorize("@PPS.hasToPolicy('" + TOP_TENANT_ADMIN + "')")
  @Operation(summary = "Query the detail of tenant.", operationId = "tenant:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping(value = "/{id}")
  public ApiLocaleResult<TenantDetailVo> detail(
      @Parameter(name = "id", description = "Tenant id", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(tenantFacade.detail(id));
  }

  @PreAuthorize("@PPS.hasToPolicy('" + TOP_TENANT_ADMIN + "')")
  @Operation(summary = "Query the list of tenant.", operationId = "tenant:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping
  public ApiLocaleResult<PageResult<TenantVo>> list(@Valid @ParameterObject TenantFindDto dto) {
    return ApiLocaleResult.success(tenantFacade.list(dto));
  }

  @PreAuthorize("@PPS.hasToPolicy('" + TOP_TENANT_ADMIN + "')")
  @Operation(summary = "Fulltext search tenant.", operationId = "tenant:search")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/search")
  public ApiLocaleResult<PageResult<TenantVo>> search(@Valid @ParameterObject TenantSearchDto dto) {
    return ApiLocaleResult.success(tenantFacade.search(dto));
  }
}
