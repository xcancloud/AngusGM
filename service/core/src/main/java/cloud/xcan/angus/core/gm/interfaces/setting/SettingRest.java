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

@Tag(name = "Setting", description = "Centralizes system-wide settings for unified control and consistency.")
@Validated
@RestController
@RequestMapping("/api/v1/setting")
public class SettingRest {

  @Resource
  private SettingFacade settingFacade;

  @Operation(summary = "Replace setting value.", operationId = "setting:value:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @PutMapping
  public ApiLocaleResult<?> replace(@Valid @RequestBody SettingValueReplaceDto dto) {
    settingFacade.replace(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Query the detail of setting value.", operationId = "setting:value:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping(value = "/{key}")
  public ApiLocaleResult<SettingValueVo> detail(
      @Parameter(name = "key", description = "Setting key", required = true) @PathVariable("key") SettingKey key) {
    return ApiLocaleResult.success(settingFacade.detail(key));
  }

}
