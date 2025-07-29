package cloud.xcan.angus.core.gm.interfaces.setting;

import cloud.xcan.angus.api.commonlink.setting.tenant.event.TesterEvent;
import cloud.xcan.angus.api.gm.indicator.to.FuncTo;
import cloud.xcan.angus.api.gm.indicator.to.PerfTo;
import cloud.xcan.angus.api.gm.indicator.to.StabilityTo;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.SettingTenantFacade;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.dto.tenant.TenantLocaleReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.to.setting.locale.LocaleTo;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.to.setting.security.SecurityTo;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.to.tenant.TenantServerApiProxyTo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "SettingTenant", description = "Tenant-level configuration management system. Manages tenant-level configurations to customize shared settings across their ecosystem for personalized tenant experiences")
@Validated
@RestController
@RequestMapping("/api/v1/setting/tenant")
public class SettingTenantRest {

  @Resource
  private SettingTenantFacade settingTenantFacade;

  @Operation(summary = "Update tenant locale configuration", operationId = "setting:tenant:locale:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Tenant locale configuration updated successfully")})
  @PutMapping("/locale")
  public ApiLocaleResult<?> localeReplace(@Valid @RequestBody TenantLocaleReplaceDto dto) {
    settingTenantFacade.localeReplace(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Get tenant locale configuration details", operationId = "setting:tenant:locale:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Tenant locale configuration retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Tenant locale configuration not found")})
  @GetMapping("/locale")
  public ApiLocaleResult<LocaleTo> localeDetail() {
    return ApiLocaleResult.success(settingTenantFacade.localeDetail());
  }

  @Operation(summary = "Update tenant security configuration", operationId = "setting:tenant:security:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Tenant security configuration updated successfully")})
  @PutMapping(value = "/security")
  public ApiLocaleResult<?> securityReplace(@Valid @RequestBody SecurityTo dto) {
    settingTenantFacade.securityReplace(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Generate tenant signup invitation code", operationId = "setting:tenant:signup:invitationCode:gen")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Tenant invitation code generated successfully")})
  @GetMapping(value = "/signup/invitationCode")
  public ApiLocaleResult<String> invitationCodeGen() {
    return ApiLocaleResult.successData(settingTenantFacade.invitationCodeGen());
  }

  @Operation(summary = "Get tenant security configuration details", operationId = "setting:tenant:security:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Tenant security configuration retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Tenant security configuration not found")})
  @GetMapping(value = "/security")
  public ApiLocaleResult<SecurityTo> securityDetail() {
    return ApiLocaleResult.success(settingTenantFacade.securityDetail());
  }

  @Operation(summary = "Update tenant API proxy configuration", operationId = "setting:tenant:proxy:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Tenant API proxy configuration updated successfully")})
  @PutMapping("/apis/proxy")
  public ApiLocaleResult<?> proxyReplace(@Valid @RequestBody TenantServerApiProxyTo dto) {
    settingTenantFacade.proxyReplace(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Get tenant API proxy configuration details", operationId = "setting:tenant:proxy:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Tenant API proxy configuration retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Tenant API proxy configuration not found")})
  @GetMapping(value = "/apis/proxy")
  public ApiLocaleResult<TenantServerApiProxyTo> proxyDetail() {
    return ApiLocaleResult.success(settingTenantFacade.proxyDetail());
  }

  @Operation(summary = "Update tenant AngusTester event configuration", operationId = "setting:tenant:tester:event:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Tenant tester event configuration updated successfully")})
  @PutMapping("/tester/event")
  public ApiLocaleResult<?> testerEventReplace(@Valid @RequestBody List<TesterEvent> dto) {
    settingTenantFacade.testerEventReplace(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Get tenant AngusTester event configuration details", operationId = "setting:tenant:tester:event:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Tenant tester event configuration retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Tenant tester event configuration not found")})
  @GetMapping(value = "/tester/event")
  public ApiLocaleResult<List<TesterEvent>> testerEventDetail() {
    return ApiLocaleResult.success(settingTenantFacade.testerEventDetail());
  }

  @Operation(summary = "Update tenant platform functionality indicators", operationId = "setting:tenant:indicator:func:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Tenant functionality indicators updated successfully"),
      @ApiResponse(responseCode = "404", description = "Tenant functionality indicators not found")})
  @PutMapping(value = "/indicator/func")
  public ApiLocaleResult<?> funcReplace(@Valid @RequestBody FuncTo funcTo) {
    settingTenantFacade.funcReplace(funcTo);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Get tenant platform functionality indicators", operationId = "setting:indicator:func:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Tenant functionality indicators retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Tenant functionality indicators not found")})
  @GetMapping(value = "/indicator/func")
  public ApiLocaleResult<FuncTo> funcDetail() {
    return ApiLocaleResult.success(settingTenantFacade.funcDetail());
  }

  @Operation(summary = "Update tenant platform performance indicators", operationId = "setting:tenant:indicator:perf:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Tenant performance indicators updated successfully"),
      @ApiResponse(responseCode = "404", description = "Tenant performance indicators not found")})
  @PutMapping(value = "/indicator/perf")
  public ApiLocaleResult<?> perfReplace(@Valid @RequestBody PerfTo perfTo) {
    settingTenantFacade.perfReplace(perfTo);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Get tenant platform performance indicators", operationId = "setting:indicator:perf:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Tenant performance indicators retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Tenant performance indicators not found")})
  @GetMapping(value = "/indicator/perf")
  public ApiLocaleResult<PerfTo> perfDetail() {
    return ApiLocaleResult.success(settingTenantFacade.perfDetail());
  }

  @Operation(summary = "Update tenant platform stability indicators", operationId = "setting:tenant:indicator:stability:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Tenant stability indicators updated successfully"),
      @ApiResponse(responseCode = "404", description = "Tenant stability indicators not found")})
  @PutMapping(value = "/indicator/stability")
  public ApiLocaleResult<?> stabilityReplace(@Valid @RequestBody StabilityTo stability) {
    settingTenantFacade.stabilityReplace(stability);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Get tenant platform stability indicators", operationId = "setting:tenant:indicator:stability:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Tenant stability indicators retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Tenant stability indicators not found")})
  @GetMapping(value = "/indicator/stability")
  public ApiLocaleResult<StabilityTo> stabilityDetail() {
    return ApiLocaleResult.success(settingTenantFacade.stabilityDetail());
  }

}
