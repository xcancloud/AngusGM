package cloud.xcan.angus.api.gm.setting;

import cloud.xcan.angus.api.gm.setting.dto.quota.QuotaCheckDto;
import cloud.xcan.angus.api.gm.setting.dto.quota.QuotaReplaceByOrderDto;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.HashSet;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "${xcan.service.gm:XCAN-ANGUSGM.BOOT}")
public interface SettingTenantQuotaRemote {

  @Operation(summary = "Authorize or cancel to authorize tenant quotas by order.", operationId = "setting:tenant:quota:byorder:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully"),
      @ApiResponse(responseCode = "404", description = "Not found resource")})
  @PatchMapping(value = "/api/v1/setting/tenant/quota/byorder")
  ApiLocaleResult<?> quotaReplaceByOrder(@Valid @RequestBody QuotaReplaceByOrderDto dto);

  @Operation(summary = "Check whether the purchase quota exceeds the upper limit.", operationId = "setting:tenant:quota:check")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully"),
      @ApiResponse(responseCode = "404", description = "Not found resource")})
  @PatchMapping(value = "/api/v1/setting/tenant/quota/check")
  ApiLocaleResult<?> quotaCheck(@Valid @RequestBody HashSet<QuotaCheckDto> dtos);

  @Operation(summary = "Check whether the new purchase quota plus the cumulative purchase quota exceeds the upper limit.", operationId = "setting:tenant:quota:expansion:check")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully"),
      @ApiResponse(responseCode = "404", description = "Not found resource")})
  @PatchMapping(value = "/api/v1/setting/tenant/quota/expansion/check")
  ApiLocaleResult<?> quotaExpansionCheck(@Valid @RequestBody HashSet<QuotaCheckDto> dtos);

}
