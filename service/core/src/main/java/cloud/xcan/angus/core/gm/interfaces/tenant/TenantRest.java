package cloud.xcan.angus.core.gm.interfaces.tenant;


import static cloud.xcan.angus.api.commonlink.UCConstant.TOP_TENANT_ADMIN;

import cloud.xcan.angus.api.gm.tenant.dto.TenantAddByMobileDto;
import cloud.xcan.angus.api.gm.tenant.dto.TenantAddDto;
import cloud.xcan.angus.api.gm.tenant.dto.TenantFindDto;
import cloud.xcan.angus.api.gm.tenant.dto.TenantLockedDto;
import cloud.xcan.angus.api.gm.tenant.dto.TenantReplaceDto;
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


@Tag(name = "Tenant", description = "Comprehensive tenant account management system. Centralizes tenant account controls, configurations, access permissions, and lifecycle management for cloud service environments")
@Conditional(CloudServiceEditionCondition.class)
@Validated
@RestController
@RequestMapping("/api/v1/tenant")
public class TenantRest {

  @Resource
  private TenantFacade tenantFacade;

  @PreAuthorize("@PPS.hasToPolicy('" + TOP_TENANT_ADMIN + "')")
  @Operation(summary = "Create new tenant account", operationId = "tenant:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Tenant account created successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<IdKey<Long, Object>> add(@Valid @RequestBody TenantAddDto dto) {
    return ApiLocaleResult.success(tenantFacade.add(dto));
  }

  @PreAuthorize("@PPS.hasToPolicy('" + TOP_TENANT_ADMIN + "')")
  @Operation(summary = "Create tenant account through mobile number registration", operationId = "tenant:signupByMobile:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Tenant account created successfully via mobile registration")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/signupByMobile")
  public ApiLocaleResult<IdKey<Long, Object>> signupByMobile(
      @Valid @RequestBody TenantAddByMobileDto dto) {
    return ApiLocaleResult.success(tenantFacade.signupByMobile(dto));
  }

  @PreAuthorize("@PPS.hasToPolicy('" + TOP_TENANT_ADMIN + "')")
  @Operation(summary = "Update existing tenant account information", operationId = "tenant:update")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Tenant account updated successfully"),
      @ApiResponse(responseCode = "404", description = "Tenant account not found")
  })
  @PatchMapping
  public ApiLocaleResult<?> update(@Valid @RequestBody TenantUpdateDto dto) {
    tenantFacade.update(dto);
    return ApiLocaleResult.success();
  }

  @PreAuthorize("@PPS.hasToPolicy('" + TOP_TENANT_ADMIN + "')")
  @Operation(summary = "Replace tenant account with new configuration", operationId = "tenant:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Tenant account replaced successfully"),
      @ApiResponse(responseCode = "404", description = "Tenant account not found")
  })
  @PutMapping
  public ApiLocaleResult<?> replace(@Valid @RequestBody TenantReplaceDto dto) {
    tenantFacade.replace(dto);
    return ApiLocaleResult.success();
  }

  @PreAuthorize("@PPS.hasToPolicy('" + TOP_TENANT_ADMIN + "')")
  @Operation(summary = "Enable or disable tenant account access", operationId = "tenant:enabled")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Tenant account status updated successfully"),
      @ApiResponse(responseCode = "404", description = "Tenant account not found")
  })
  @PatchMapping("/enabled")
  public ApiLocaleResult<?> enabled(@Valid @RequestBody EnabledOrDisabledDto dto) {
    tenantFacade.enabled(dto);
    return ApiLocaleResult.success();
  }

  @PreAuthorize("@PPS.hasToPolicy('" + TOP_TENANT_ADMIN + "')")
  @Operation(summary = "Lock or unlock tenant account access", operationId = "tenant:locked")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Tenant account lock status updated successfully"),
      @ApiResponse(responseCode = "404", description = "Tenant account not found")
  })
  @PatchMapping("/locked")
  public ApiLocaleResult<?> locked(@Valid @RequestBody TenantLockedDto dto) {
    tenantFacade.locked(dto);
    return ApiLocaleResult.success();
  }

  @PreAuthorize("@PPS.hasToPolicy('" + TOP_TENANT_ADMIN + "')")
  @Operation(summary = "Get detailed tenant account information", operationId = "tenant:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Tenant account details retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Tenant account not found")})
  @GetMapping(value = "/{id}")
  public ApiLocaleResult<TenantDetailVo> detail(
      @Parameter(name = "id", description = "Tenant account unique identifier", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(tenantFacade.detail(id));
  }

  @PreAuthorize("@PPS.hasToPolicy('" + TOP_TENANT_ADMIN + "')")
  @Operation(summary = "Get paginated list of tenant accounts", operationId = "tenant:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Tenant account list retrieved successfully")})
  @GetMapping
  public ApiLocaleResult<PageResult<TenantVo>> list(@Valid @ParameterObject TenantFindDto dto) {
    return ApiLocaleResult.success(tenantFacade.list(dto));
  }

}
