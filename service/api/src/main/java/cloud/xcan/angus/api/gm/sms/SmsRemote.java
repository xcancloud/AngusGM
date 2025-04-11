package cloud.xcan.angus.api.gm.sms;

import cloud.xcan.angus.api.gm.sms.dto.SmsSendDto;
import cloud.xcan.angus.api.gm.sms.dto.SmsVerificationCodeCheckDto;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "${xcan.service.gm:XCAN-ANGUSGM.BOOT}")
public interface SmsRemote {

  @Operation(description = "Send sms", operationId = "sms:send")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully sent")})
  @PostMapping("/api/v1/sms/send")
  ApiLocaleResult<?> send(@Valid @RequestBody SmsSendDto dto);

  @Operation(description = "Check sms code", operationId = "sms:verificationCode:check")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully check")})
  @GetMapping("/api/v1/sms/verificationCode/check")
  ApiLocaleResult<?> check(@Valid @SpringQueryMap SmsVerificationCodeCheckDto dto);

}
