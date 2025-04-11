package cloud.xcan.angus.api.gm.setting;

import cloud.xcan.angus.api.gm.setting.dto.quota.QuotaReplaceByOrderDto;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "${xcan.service.gm:XCAN-ANGUSGM.BOOT}")
public interface SettingTenantQuotaDoorRemote {

  @Operation(description = "Authorize or deauthorize tenant quotas by order", operationId = "setting:tenant:quota:byorder:replace:inner")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully"),
      @ApiResponse(responseCode = "404", description = "Not found resource")})
  @PatchMapping(value = "/innerapi/v1/setting/tenant/quota/byorder")
  ApiLocaleResult<?> quotaReplaceByOrder(@Valid @RequestBody QuotaReplaceByOrderDto dto);

}
