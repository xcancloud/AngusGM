package cloud.xcan.angus.core.gm.interfaces.setting;

import cloud.xcan.angus.api.commonlink.setting.SettingKey;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.SettingFacade;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.dto.SettingValueReplaceDto;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.vo.SettingValueVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Setting", description = "System-wide configuration management. Centralizes system-wide settings for unified control and consistency across all system components")
@Validated
@RestController
@RequestMapping("/api/v1/setting")
public class SettingRest {

  @Resource
  private SettingFacade settingFacade;

  @Operation(summary = "Update system-wide setting configuration", operationId = "setting:value:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "System setting configuration updated successfully"),
      @ApiResponse(responseCode = "404", description = "System setting not found")})
  @PutMapping
  public ApiLocaleResult<?> replace(@Valid @RequestBody SettingValueReplaceDto dto) {
    settingFacade.replace(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Get detailed system setting configuration", operationId = "setting:value:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "System setting configuration retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "System setting not found")})
  @GetMapping(value = "/{key}")
  public ApiLocaleResult<SettingValueVo> detail(
      @Parameter(name = "key", description = "System setting key identifier", required = true) @PathVariable("key") SettingKey key) {
    return ApiLocaleResult.success(settingFacade.detail(key));
  }

}
