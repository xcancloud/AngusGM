package cloud.xcan.angus.core.gm.interfaces.user;

import cloud.xcan.angus.api.gm.tenant.vo.TenantDetailVo;
import cloud.xcan.angus.api.gm.user.dto.UserCurrentUpdateDto;
import cloud.xcan.angus.api.gm.user.vo.UserCurrentDetailVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.UserCurrentFacade;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.current.CurrentEmailCheckDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.current.CurrentEmailSendDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.current.CurrentEmailUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.current.CurrentMobileCheckDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.current.CurrentMobileUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.current.CurrentSmsSendDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.current.CheckSecretVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.spec.annotations.TenantClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "UserCurrent", description = "Personal center current user operation api entry.")
@TenantClient
@Validated
@RestController
@RequestMapping("/api/v1/user/current")
public class UserCurrentRest {

  @Resource
  private UserCurrentFacade userCurrentFacade;

  @Operation(description = "Query tenant of current user.", operationId = "user:current:tenant:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping(value = "/tenant")
  public ApiLocaleResult<TenantDetailVo> tenantDetail() {
    return ApiLocaleResult.success(userCurrentFacade.tenantDetail());
  }

  @Operation(description = "Update the current user.", operationId = "user:current:update")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Updated successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @PatchMapping
  public ApiLocaleResult<?> currentUpdate(@Valid @RequestBody UserCurrentUpdateDto dto) {
    userCurrentFacade.currentUpdate(dto);
    return ApiLocaleResult.success();
  }

  @Operation(description = "Query the detail of current user.", operationId = "user:current:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping
  public ApiLocaleResult<UserCurrentDetailVo> currentDetail() {
    return ApiLocaleResult.success(userCurrentFacade.currentDetail());
  }

  @Operation(description = "Send sms verification code to current user.", operationId = "user:current:sms:send")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully sent")})
  @PostMapping(value = "/sms/send")
  public ApiLocaleResult<?> sendSms(@Valid @RequestBody CurrentSmsSendDto dto) {
    userCurrentFacade.sendSms(dto);
    return ApiLocaleResult.success();
  }

  @Operation(description = "Check sms verification code of current user.", operationId = "user:current:sms:check")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully check")})
  @GetMapping(value = "/sms/check")
  public ApiLocaleResult<CheckSecretVo> checkSms(@Valid @ParameterObject CurrentMobileCheckDto dto) {
    return ApiLocaleResult.success(userCurrentFacade.checkSms(dto));
  }

  @Operation(description = "Update the mobile of current user.", operationId = "user:current:mobile:update")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Updated successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @PatchMapping(value = "/mobile")
  public ApiLocaleResult<?> updateMobile(@Valid @RequestBody CurrentMobileUpdateDto dto) {
    userCurrentFacade.updateMobile(dto);
    return ApiLocaleResult.success();
  }

  @Operation(description = "Send email verification code to current user.", operationId = "user:current:email:send")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully sent")})
  @PostMapping(value = "/email/send")
  public ApiLocaleResult<?> sendEmail(@Valid @RequestBody CurrentEmailSendDto dto) {
    userCurrentFacade.sendEmail(dto);
    return ApiLocaleResult.success();
  }

  @Operation(description = "Check email verification code of current user.", operationId = "user:current:email:check")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully check")})
  @GetMapping(value = "/email/check")
  public ApiLocaleResult<CheckSecretVo> checkEmail(@Valid @ParameterObject CurrentEmailCheckDto dto) {
    return ApiLocaleResult.success(userCurrentFacade.checkEmail(dto));
  }

  @Operation(description = "Update the email of current user.", operationId = "user:current:email:update")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Updated successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @PatchMapping(value = "/email")
  public ApiLocaleResult<?> updateEmail(@Valid @RequestBody CurrentEmailUpdateDto dto) {
    userCurrentFacade.updateEmail(dto);
    return ApiLocaleResult.success();
  }

}
