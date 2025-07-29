package cloud.xcan.angus.core.gm.interfaces.email;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;

import cloud.xcan.angus.api.gm.email.dto.EmailSendDto;
import cloud.xcan.angus.api.gm.email.dto.EmailVerificationCodeCheckDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.EmailFacade;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailFindDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailTestDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailDetailVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "Email", description = "REST API endpoints for email message delivery operations and audit logging for tracking sent communications")
@Validated
@RestController
@RequestMapping("/api/v1/email")
public class EmailRest {

  @Resource
  private EmailFacade emailFacade;

  @Operation(summary = "Send email message", operationId = "email:send")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Email sent successfully")})
  @ResponseStatus(HttpStatus.OK)
  @PostMapping
  public ApiLocaleResult<?> send(@Valid @RequestBody EmailSendDto dto) {
    emailFacade.send(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Test email server configuration", operationId = "email:test")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Email server test completed successfully")})
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/server/test")
  public ApiLocaleResult<?> mailTest(@Valid @RequestBody EmailTestDto dto) {
    emailFacade.mailTest(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Delete multiple emails", operationId = "email:delete")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Emails deleted successfully")})
  @DeleteMapping
  public void delete(
      @Valid @Size(max = MAX_BATCH_SIZE) @RequestParam("ids") HashSet<Long> ids) {
    emailFacade.delete(ids);
  }

  @Operation(summary = "Validate email verification code", operationId = "email:verificationCode:check")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Verification code validated successfully")})
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/verificationCode/check")
  public ApiLocaleResult<?> verificationCodeCheck(@Valid @ParameterObject EmailVerificationCodeCheckDto dto) {
    emailFacade.verificationCodeCheck(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Retrieve detailed email information", operationId = "email:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Email details retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Email not found")})
  @ResponseStatus(HttpStatus.OK)
  @GetMapping(value = "/{id}")
  public ApiLocaleResult<EmailDetailVo> detail(
      @Parameter(name = "id", description = "Email identifier", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(emailFacade.detail(id));
  }

  @Operation(summary = "Retrieve email list with filtering and pagination", operationId = "email:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Email list retrieved successfully")})
  @ResponseStatus(HttpStatus.OK)
  @GetMapping
  public ApiLocaleResult<PageResult<EmailDetailVo>> list(@Valid @ParameterObject EmailFindDto dto) {
    return ApiLocaleResult.success(emailFacade.list(dto));
  }

}
