package cloud.xcan.angus.core.gm.interfaces.setting;

import cloud.xcan.angus.api.gm.setting.dto.quota.QuotaCheckDto;
import cloud.xcan.angus.api.gm.setting.dto.quota.QuotaReplaceByOrderDto;
import cloud.xcan.angus.api.gm.setting.dto.quota.QuotaReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.SettingTenantQuotaFacade;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.dto.TenantQuotaFindDto;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.vo.tenant.TenantQuotaDetailVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.HashSet;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "SettingTenantQuota", description = "Tenant resource quota management system. Assigns and initializes tenant resource quotas based on predefined policies or payment orders for comprehensive resource control")
@Validated
@RestController
@RequestMapping("/api/v1/setting/tenant/quota")
public class SettingTenantQuotaRest {

  @Resource
  private SettingTenantQuotaFacade settingTenantQuotaFacade;

  @Operation(summary = "Update tenant resource quota allocation", operationId = "setting:tenant:quota:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Tenant quota allocation updated successfully"),
      @ApiResponse(responseCode = "404", description = "Tenant quota configuration not found")})
  @PutMapping(value = "/{name}/{quota}")
  public ApiLocaleResult<?> quotaReplace(
      @Parameter(name = "name", description = "Tenant quota resource identifier", required = true) @PathVariable("name") String name,
      @Parameter(name = "quota", description = "Tenant quota allocation value", required = true) @PathVariable("quota") Long quota) {
    settingTenantQuotaFacade.quotaReplace(name, quota);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Update multiple tenant resource quotas in batch", operationId = "setting:tenant:quota:replace:batch")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Tenant quota batch update completed successfully"),
      @ApiResponse(responseCode = "404", description = "Tenant quota configuration not found")})
  @PutMapping
  public ApiLocaleResult<?> quotaReplaceBatch(@Valid @RequestBody HashSet<QuotaReplaceDto> dto) {
    settingTenantQuotaFacade.quotaReplaceBatch(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Manage tenant quotas based on order status", operationId = "setting:tenant:quota:byorder:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Tenant quota order management completed successfully"),
      @ApiResponse(responseCode = "404", description = "Tenant quota configuration not found")})
  @PatchMapping(value = "/byorder")
  public ApiLocaleResult<?> quotaReplaceByOrder(@Valid @RequestBody QuotaReplaceByOrderDto dto) {
    settingTenantQuotaFacade.quotaReplaceByOrder(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Initialize new quota configurations for all tenants", operationId = "setting:tenant:quota:new:init")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "New quota initialization completed successfully"),
      @ApiResponse(responseCode = "404", description = "Tenant quota configuration not found")})
  @PutMapping(value = "/{name}/new/init")
  public ApiLocaleResult<Long> newQuotaInit(
      @Parameter(name = "name", description = "Tenant quota resource identifier", required = true) @PathVariable("name") String name) {
    return ApiLocaleResult.success(settingTenantQuotaFacade.newQuotaInit(name));
  }

  @Operation(summary = "Validate quota purchase against upper limits", operationId = "setting:tenant:quota:check")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Quota validation completed successfully"),
      @ApiResponse(responseCode = "404", description = "Tenant quota configuration not found")})
  @PatchMapping(value = "/check")
  public ApiLocaleResult<?> quotaCheck(@Valid @RequestBody HashSet<QuotaCheckDto> dto) {
    settingTenantQuotaFacade.quotaCheck(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Validate expanded quota against cumulative limits", operationId = "setting:tenant:quota:expansion:check")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Quota expansion validation completed successfully"),
      @ApiResponse(responseCode = "404", description = "Tenant quota configuration not found")})
  @PatchMapping(value = "/expansion/check")
  public ApiLocaleResult<?> quotaExpansionCheck(@Valid @RequestBody HashSet<QuotaCheckDto> dto) {
    settingTenantQuotaFacade.quotaExpansionCheck(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Get detailed tenant quota configuration", operationId = "setting:tenant:quota:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Tenant quota details retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Tenant quota configuration not found")})
  @GetMapping(value = "/{name}")
  public ApiLocaleResult<TenantQuotaDetailVo> detail(
      @Parameter(name = "name", description = "Tenant quota resource identifier", required = true) @PathVariable("name") String name) {
    return ApiLocaleResult.success(settingTenantQuotaFacade.detail(name));
  }

  @Operation(summary = "Get available quota application list", operationId = "setting:tenant:quota:app:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Quota application list retrieved successfully")})
  @GetMapping("/app")
  public ApiLocaleResult<List<String>> appList() {
    return ApiLocaleResult.success(settingTenantQuotaFacade.appList());
  }

  @Operation(summary = "Get paginated list of tenant quotas", operationId = "setting:tenant:quota:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Tenant quota list retrieved successfully")})
  @GetMapping
  public ApiLocaleResult<PageResult<TenantQuotaDetailVo>> list(@Valid @ParameterObject TenantQuotaFindDto dto) {
    return ApiLocaleResult.success(settingTenantQuotaFacade.list(dto));
  }


}
