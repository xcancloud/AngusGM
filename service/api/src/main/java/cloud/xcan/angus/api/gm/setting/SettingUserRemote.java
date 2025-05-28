package cloud.xcan.angus.api.gm.setting;

import cloud.xcan.angus.api.gm.setting.vo.UserPreferenceVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "${xcan.service.gm:XCAN-ANGUSGM.BOOT}")
public interface SettingUserRemote {

  @Operation(summary = "Query the preference setting of the current user.", operationId = "setting:user:preference:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping(value = "/api/v1/setting/user/preference")
  ApiLocaleResult<UserPreferenceVo> preferenceDetail();

}
