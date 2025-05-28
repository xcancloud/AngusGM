package cloud.xcan.angus.core.gm.interfaces.setting;

import cloud.xcan.angus.api.gm.setting.dto.quota.QuotaReplaceByOrderDto;
import cloud.xcan.angus.core.gm.interfaces.setting.facade.SettingTenantQuotaFacade;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@PreAuthorize("hasAuthority('SCOPE_inner_api_trust')")
@Tag(name = "SettingTenantQuotaInner", description = "Automatically grants or revokes resource quotas linked to payment verification and order status.")
@Validated
@RestController
@RequestMapping("/innerapi/v1/setting/tenant/quota")
public class SettingTenantQuotaDoorRest {

  @Resource
  private SettingTenantQuotaFacade settingTenantQuotaFacade;

  @Operation(summary = "Authorize or de-authorize tenant quotas by order", operationId = "setting:tenant:quota:byorder:replace:inner")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully"),
      @ApiResponse(responseCode = "404", description = "Not found resource")})
  @PatchMapping(value = "/byorder")
  public ApiLocaleResult<?> quotaReplaceByOrder(@Valid @RequestBody QuotaReplaceByOrderDto dto) {
    settingTenantQuotaFacade.quotaReplaceByOrder(dto);
    return ApiLocaleResult.success();
  }

}
