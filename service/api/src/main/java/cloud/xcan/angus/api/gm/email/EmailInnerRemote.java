package cloud.xcan.angus.api.gm.email;

import cloud.xcan.angus.api.gm.email.dto.EmailSendDto;
import cloud.xcan.angus.api.gm.email.dto.EmailVerificationCodeCheckDto;
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
public interface EmailInnerRemote {

  @Operation(summary = "Send email", operationId = "email:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Created successfully")})
  @PostMapping("/innerapi/v1/email")
  ApiLocaleResult<?> send(@Valid @RequestBody EmailSendDto dto);

  @Operation(summary = "Check email verification code", operationId = "email:verificationCode:check")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Created successfully"),
      @ApiResponse(responseCode = "404", description = "Email does not exist, sendId error")
  })
  @GetMapping("/innerapi/v1/email/verificationCode/check")
  ApiLocaleResult<?> check(@Valid @SpringQueryMap EmailVerificationCodeCheckDto dto);

}
