package cloud.xcan.angus.core.gm.interfaces.setting;

import cloud.xcan.angus.core.gm.interfaces.setting.facade.SettingTenantFacade;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.dto.tenant.TenantLocaleReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.to.locale.LocaleTo;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.to.security.SecurityTo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Setting Tenant", description = "Tenant-level configuration management system. Manages tenant-level configurations to customize shared settings across their ecosystem for personalized tenant experiences")
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

}
