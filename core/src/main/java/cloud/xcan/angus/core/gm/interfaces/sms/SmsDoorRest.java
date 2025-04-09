package cloud.xcan.angus.core.gm.interfaces.sms;

import cloud.xcan.angus.api.gm.sms.dto.SmsSendDto;
import cloud.xcan.angus.api.gm.sms.dto.SmsVerificationCodeCheckDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.SmsDoorFacade;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "SmsDoor", description = "Provides programmatic sms send and validity verification for cross-system service call.")
@Validated
@RestController
@RequestMapping("/innerapi/v1/sms")
public class SmsDoorRest {

  @Resource
  private SmsDoorFacade smsDoorFacade;

  @Operation(description = "Send sms.", operationId = "sms:send:inner")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Sent successfully")})
  @PostMapping("/send")
  public ApiLocaleResult<?> send(@Valid @RequestBody SmsSendDto dto) {
    smsDoorFacade.sendSms(dto);
    return ApiLocaleResult.success();
  }

  @Operation(description = "Check sms verification code.", operationId = "sms:verificationCode:check:inner")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully check")})
  @GetMapping("/verificationCode/check")
  public ApiLocaleResult<?> checkVerificationCode(@Valid SmsVerificationCodeCheckDto dto) {
    smsDoorFacade.checkVerificationCode(dto);
    return ApiLocaleResult.success();
  }
}
