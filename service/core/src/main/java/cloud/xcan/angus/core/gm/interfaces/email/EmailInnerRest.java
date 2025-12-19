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
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@PreAuthorize("hasAuthority('SCOPE_inner_api_trust')")
@Tag(name = "Email Internal", description = "Internal REST API endpoints for programmatic email delivery and real-time verification code validation for cross-system service calls")
@Validated
@RestController
@RequestMapping("/innerapi/v1/email")
public class EmailInnerRest {

  @Resource
  private EmailDoorFacade emailDoorFacade;

  @Operation(summary = "Send email from external systems", operationId = "email:send:inner")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Email sent successfully")})
  @ResponseStatus(HttpStatus.OK)
  @PostMapping
  public ApiLocaleResult<?> send(@Valid @RequestBody EmailSendDto dto) {
    emailDoorFacade.send(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Validate email verification code", operationId = "email:verificationCode:check:inner")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Verification code validated successfully")})
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/verificationCode/check")
  public ApiLocaleResult<?> checkVerificationCode(
      @Valid @ParameterObject EmailVerificationCodeCheckDto dto) {
    emailDoorFacade.checkVerificationCode(dto);
    return ApiLocaleResult.success();
  }

}
