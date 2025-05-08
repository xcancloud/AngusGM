package cloud.xcan.angus.core.gm.interfaces.setting;

import cloud.xcan.angus.api.gm.setting.dto.quota.QuotaCheckDto;
import cloud.xcan.angus.api.gm.setting.dto.quota.QuotaReplaceByOrderDto;
import cloud.xcan.angus.api.gm.setting.dto.quota.QuotaReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.SettingTenantQuotaFacade;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.dto.TenantQuotaFindDto;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.dto.TenantQuotaSearchDto;
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

@Tag(name = "SettingTenantQuota", description = "Assigns and initializes tenant resource quotas based on predefined policies or payment order.")
@Validated
@RestController
@RequestMapping("/api/v1/setting/tenant/quota")
public class SettingTenantQuotaRest {

  @Resource
  private SettingTenantQuotaFacade settingTenantQuotaFacade;

  @Operation(description = "Replace the quota of tenant.", operationId = "setting:tenant:quota:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully"),
      @ApiResponse(responseCode = "404", description = "Not found resource")})
  @PutMapping(value = "/{name}/{quota}")
  public ApiLocaleResult<?> quotaReplace(
      @Parameter(name = "name", description = "Tenant quota resource name", required = true) @PathVariable("name") String name,
      @Parameter(name = "quota", description = "Tenant quota value", required = true) @PathVariable("quota") Long quota) {
    settingTenantQuotaFacade.quotaReplace(name, quota);
    return ApiLocaleResult.success();
  }

  @Operation(description = "Replace the quotas of tenant.", operationId = "setting:tenant:quota:replace:batch")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully"),
      @ApiResponse(responseCode = "404", description = "Not found resource")})
  @PutMapping
  public ApiLocaleResult<?> quotaReplaceBatch(@Valid @RequestBody HashSet<QuotaReplaceDto> dto) {
    settingTenantQuotaFacade.quotaReplaceBatch(dto);
    return ApiLocaleResult.success();
  }

  @Operation(description = "Authorize or de-authorize tenant quotas by order.", operationId = "setting:tenant:quota:byorder:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully"),
      @ApiResponse(responseCode = "404", description = "Not found resource")})
  @PatchMapping(value = "/byorder")
  public ApiLocaleResult<?> quotaReplaceByOrder(@Valid @RequestBody QuotaReplaceByOrderDto dto) {
    settingTenantQuotaFacade.quotaReplaceByOrder(dto);
    return ApiLocaleResult.success();
  }

  @Operation(description = "Initialize new quotas for all tenants.", operationId = "setting:tenant:quota:new:init")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully"),
      @ApiResponse(responseCode = "404", description = "Not found resource")})
  @PutMapping(value = "/{name}/new/init")
  public ApiLocaleResult<Long> newQuotaInit(
      @Parameter(name = "name", description = "Tenant quota resource name", required = true) @PathVariable("name") String name) {
    return ApiLocaleResult.success(settingTenantQuotaFacade.newQuotaInit(name));
  }

  @Operation(description = "Check whether the purchase quota exceeds the upper limit.", operationId = "setting:tenant:quota:check")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully"),
      @ApiResponse(responseCode = "404", description = "Not found resource")})
  @PatchMapping(value = "/check")
  public ApiLocaleResult<?> quotaCheck(@Valid @RequestBody HashSet<QuotaCheckDto> dto) {
    settingTenantQuotaFacade.quotaCheck(dto);
    return ApiLocaleResult.success();
  }

  @Operation(description = "Check whether the new purchase quota plus the cumulative purchase quota exceeds the upper limit.",
      operationId = "setting:tenant:quota:expansion:check")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully"),
      @ApiResponse(responseCode = "404", description = "Not found resource")})
  @PatchMapping(value = "/expansion/check")
  public ApiLocaleResult<?> quotaExpansionCheck(@Valid @RequestBody HashSet<QuotaCheckDto> dto) {
    settingTenantQuotaFacade.quotaExpansionCheck(dto);
    return ApiLocaleResult.success();
  }

  @Operation(description = "Query the quota detail of tenant.", operationId = "setting:tenant:quota:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Not found resource")})
  @GetMapping(value = "/{name}")
  public ApiLocaleResult<TenantQuotaDetailVo> detail(
      @Parameter(name = "name", description = "Tenant quota resource name", required = true) @PathVariable("name") String name) {
    return ApiLocaleResult.success(settingTenantQuotaFacade.detail(name));
  }

  @Operation(description = "Query the quota application list of tenant.", operationId = "setting:tenant:quota:app:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/app")
  public ApiLocaleResult<List<String>> appList() {
    return ApiLocaleResult.success(settingTenantQuotaFacade.appList());
  }

  @Operation(description = "Query the quota list of tenant.", operationId = "setting:tenant:quota:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping
  public ApiLocaleResult<PageResult<TenantQuotaDetailVo>> list(@Valid @ParameterObject TenantQuotaFindDto dto) {
    return ApiLocaleResult.success(settingTenantQuotaFacade.list(dto));
  }

  @Operation(description = "Fulltext search the quota of tenant.", operationId = "setting:tenant:quota:search")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/search")
  public ApiLocaleResult<PageResult<TenantQuotaDetailVo>> search(@Valid @ParameterObject TenantQuotaSearchDto dto) {
    return ApiLocaleResult.success(settingTenantQuotaFacade.search(dto));
  }

}
