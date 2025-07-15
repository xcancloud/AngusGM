package cloud.xcan.angus.core.gm.interfaces.tenant;


import cloud.xcan.angus.core.gm.interfaces.tenant.facade.TenantSignFacade;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.dto.SignCancelSmsConfirmDto;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.spec.annotations.TenantClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "TenantSign", description = "Processes secure tenant account termination with data cleanup and permission revocation")
@TenantClient
@Validated
@RestController
@RequestMapping("/api/v1/tenant/sign")
public class TenantSignRest {

  @Resource
  private TenantSignFacade tenantSignFacade;

  @Operation(summary = "Cancel tenant account deletion request", operationId = "tenant:sign:cancel:revoke")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully cancel signCancel")})
  @PostMapping("/cancel/revoke")
  public ApiLocaleResult<?> cancelSignInvoke() {
    tenantSignFacade.cancelSignInvoke();
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Send SMS verification for tenant account deletion", operationId = "tenant:sign:cancel:sms:send")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully sent signCancelSms")})
  @GetMapping("/cancel/sms/send")
  public ApiLocaleResult<?> signCancelSmsSend() {
    tenantSignFacade.signCancelSmsSend();
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Confirm SMS code to finalize tenant account deletion", operationId = "tenant:sign:cancel:sms:confirm")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully check")})
  @GetMapping("/cancel/sms/confirm")
  public ApiLocaleResult<?> signCancelSmsCheck(@Valid SignCancelSmsConfirmDto dto) {
    tenantSignFacade.signCancelSmsConfirm(dto);
    return ApiLocaleResult.success();
  }

}
