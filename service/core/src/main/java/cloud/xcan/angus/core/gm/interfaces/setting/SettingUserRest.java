package cloud.xcan.angus.core.gm.interfaces.setting;

import cloud.xcan.angus.api.enums.SocialType;
import cloud.xcan.angus.api.gm.setting.vo.UserApiProxyVo;
import cloud.xcan.angus.api.gm.setting.vo.UserPreferenceVo;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.SettingUserFacade;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.dto.user.UserApiClientProxyUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.dto.user.UserApiProxyEnabledDto;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.dto.user.UserPreferenceUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.vo.user.UserSocialBindingVo;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "SettingUser", description = "Provides user-specific configuration access for personalized settings within authorized limits")
@Validated
@RestController
@RequestMapping("/api/v1/setting/user")
public class SettingUserRest {

  @Resource
  private SettingUserFacade settingUserFacade;

  @Operation(summary = "Update the preference setting of the current user", operationId = "setting:user:preference:update")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Updated successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @PatchMapping(value = "/preference")
  public ApiLocaleResult<?> preferenceUpdate(@Valid @RequestBody UserPreferenceUpdateDto dto) {
    settingUserFacade.preferenceUpdate(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Query the preference setting of the current user", operationId = "setting:user:preference:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping(value = "/preference")
  public ApiLocaleResult<UserPreferenceVo> preferenceDetail() {
    return ApiLocaleResult.success(settingUserFacade.preferenceDetail());
  }

  @Operation(summary = "Update the apis proxy of the current user", operationId = "setting:user:apis:proxy:update")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Updated successfully")})
  @PatchMapping("/apis/proxy")
  public ApiLocaleResult<?> proxyUpdate(@Valid @RequestBody UserApiClientProxyUpdateDto dto) {
    settingUserFacade.proxyUpdate(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Enable the apis proxy of the current user", operationId = "setting:user:apis:proxy:enabled")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Updated successfully")})
  @PatchMapping("/apis/proxy/enabled")
  public ApiLocaleResult<?> proxyEnabled(@Valid @RequestBody UserApiProxyEnabledDto dto) {
    settingUserFacade.proxyEnabled(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Query the apis proxy of the current user", operationId = "setting:user:apis:proxy:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping(value = "/apis/proxy")
  public ApiLocaleResult<UserApiProxyVo> proxyDetail() {
    return ApiLocaleResult.success(settingUserFacade.proxyDetail());
  }

  @Operation(summary = "Unbind social of the current user", operationId = "setting:user:social:unbind")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Unbind successfully")})
  @PostMapping(value = "/social/unbind")
  public ApiLocaleResult<?> socialUnbind(
      @Parameter(name = "type", description = "Social type", required = true) @RequestParam("type") SocialType type) {
    settingUserFacade.socialUnbind(type);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Query the binding social information of the current user", operationId = "setting:user:social:binding:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved binding")})
  @GetMapping(value = "/social/binding")
  public ApiLocaleResult<UserSocialBindingVo> socialBindDetail() {
    return ApiLocaleResult.success(settingUserFacade.socialBindDetail());
  }

}
