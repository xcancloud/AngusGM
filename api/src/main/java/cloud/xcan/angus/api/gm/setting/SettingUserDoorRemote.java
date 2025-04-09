package cloud.xcan.angus.api.gm.setting;


import cloud.xcan.angus.api.gm.setting.dto.UserSocialUpdateDto;
import cloud.xcan.angus.api.gm.setting.vo.UserApiProxyVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "${xcan.service.gm:ANGUSGM}")
public interface SettingUserDoorRemote {

  @Operation(description = "Bind the social of the current user.", operationId = "setting:user:social:bind:door")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Updated successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @PatchMapping(value = "/innerapi/v1/setting/user/social/binding")
  ApiLocaleResult<?> socialBindingUpdate(@Valid @RequestBody UserSocialUpdateDto dto);

  @Operation(description = "Query the apis proxy of the current user.", operationId = "setting:user:apis:proxy:detail:door")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping(value = "/innerapi/v1/setting/user/apis/proxy")
  ApiLocaleResult<UserApiProxyVo> proxyDetail();

}
