package cloud.xcan.angus.core.gm.interfaces.setting;


import cloud.xcan.angus.api.gm.setting.dto.UserSocialUpdateDto;
import cloud.xcan.angus.api.gm.setting.vo.UserApiProxyVo;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.SettingUserFacade;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@PreAuthorize("hasAuthority('SCOPE_inner_api_trust')")
@Tag(name = "SettingUserInner", description = "Handles internal system access to user configurations for administrative or automated adjustments.")
@Validated
@RestController
@RequestMapping("/innerapi/v1/setting/user")
public class SettingUserInnerRest {

  @Resource
  private SettingUserFacade settingUserFacade;

  @Operation(summary = "Bind the social of the current user.", operationId = "setting:user:social:bind:inner")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Updated successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @PatchMapping(value = "/social/binding")
  public ApiLocaleResult<?> socialBindingUpdate(@Valid @RequestBody UserSocialUpdateDto dto) {
    settingUserFacade.socialBindingUpdate(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Query the apis proxy of the current user.", operationId = "setting:user:apis:proxy:detail:inner")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping(value = "/apis/proxy")
  public ApiLocaleResult<UserApiProxyVo> proxyDetailByInner(
      @Parameter(name = "tenantId", description = "Query tenant id.", required = true)
      @RequestParam(value = "tenantId", required = true) Long tenantId) {
    return ApiLocaleResult.success(settingUserFacade.proxyDetailByInner(tenantId));
  }

}
