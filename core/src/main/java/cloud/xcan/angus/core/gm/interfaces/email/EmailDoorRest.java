package cloud.xcan.angus.core.gm.interfaces.email;

import cloud.xcan.angus.api.gm.email.dto.EmailSendDto;
import cloud.xcan.angus.api.gm.email.dto.EmailVerificationCodeCheckDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.EmailDoorFacade;
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


@Tag(name = "EmailDoor", description = "Provides programmatic email send and real-time validity verification for cross-system service call.")
@Validated
@RestController
@RequestMapping("/innerapi/v1/email")
public class EmailDoorRest {

  @Resource
  private EmailDoorFacade emailDoorFacade;

  @Operation(description = "Send email.", operationId = "email:send:door")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Sent successfully")})
  @PostMapping
  public ApiLocaleResult<?> send(@Valid @RequestBody EmailSendDto dto) {
    emailDoorFacade.send(dto);
    return ApiLocaleResult.success();
  }

  @Operation(description = "Check the email verification code is valid.", operationId = "email:verificationCode:check:door")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully check")})
  @GetMapping("/verificationCode/check")
  public ApiLocaleResult<?> checkVerificationCode(@Valid EmailVerificationCodeCheckDto dto) {
    emailDoorFacade.checkVerificationCode(dto);
    return ApiLocaleResult.success();
  }

}
