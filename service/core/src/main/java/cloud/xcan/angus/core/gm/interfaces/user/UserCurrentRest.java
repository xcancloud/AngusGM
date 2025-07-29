package cloud.xcan.angus.core.gm.interfaces.user;

import cloud.xcan.angus.api.enums.EditionType;
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
import cloud.xcan.angus.remote.InfoScope;
import cloud.xcan.angus.spec.annotations.TenantClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "UserCurrent", description = "Personal center operations for current authenticated user, including profile management, contact information updates, and verification services")
@TenantClient
@Validated
@RestController
@RequestMapping("/api/v1/user/current")
public class UserCurrentRest {

  @Resource
  private UserCurrentFacade userCurrentFacade;

  @Operation(summary = "Get current user's tenant information", operationId = "user:current:tenant:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Tenant information retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Tenant not found")})
  @GetMapping(value = "/tenant")
  public ApiLocaleResult<TenantDetailVo> tenantDetail() {
    return ApiLocaleResult.success(userCurrentFacade.tenantDetail());
  }

  @Operation(summary = "Update current user's profile information", operationId = "user:current:update")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
      @ApiResponse(responseCode = "404", description = "User not found")})
  @PatchMapping
  public ApiLocaleResult<?> currentUpdate(@Valid @RequestBody UserCurrentUpdateDto dto) {
    userCurrentFacade.currentUpdate(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Get current user's detailed profile information", operationId = "user:current:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User details retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "User not found")})
  @GetMapping
  public ApiLocaleResult<UserCurrentDetailVo> currentDetail(
      @Parameter(name = "infoScope", description = "Scope of information to retrieve (BASIC, DETAILED, FULL). Defaults to BASIC", required = false) @RequestParam(value = "infoScope", required = false) InfoScope infoScope,
      @Parameter(name = "appCode", description = "Application identifier code for context-specific data", required = false) @RequestParam(value = "appCode", required = false) String appCode,
      @Parameter(name = "editionType", description = "Application edition type (COMMUNITY, ENTERPRISE, etc.)", required = false) @RequestParam(value = "editionType", required = false) EditionType editionType,
      Principal principal) {
    return ApiLocaleResult.success(userCurrentFacade.currentDetail(infoScope, appCode, editionType, principal));
  }

  @Operation(summary = "Send SMS verification code to current user's mobile number", operationId = "user:current:sms:send")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "SMS verification code sent successfully")})
  @PostMapping(value = "/sms/send")
  public ApiLocaleResult<?> sendSms(@Valid @RequestBody CurrentSmsSendDto dto) {
    userCurrentFacade.sendSms(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Verify SMS code for current user's mobile number", operationId = "user:current:sms:check")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "SMS verification completed successfully")})
  @GetMapping(value = "/sms/check")
  public ApiLocaleResult<CheckSecretVo> checkSms(
      @Valid @ParameterObject CurrentMobileCheckDto dto) {
    return ApiLocaleResult.success(userCurrentFacade.checkSms(dto));
  }

  @Operation(summary = "Update current user's mobile phone number", operationId = "user:current:mobile:update")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Mobile number updated successfully"),
      @ApiResponse(responseCode = "404", description = "User not found")})
  @PatchMapping(value = "/mobile")
  public ApiLocaleResult<?> updateMobile(@Valid @RequestBody CurrentMobileUpdateDto dto) {
    userCurrentFacade.updateMobile(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Send email verification code to current user's email address", operationId = "user:current:email:send")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Email verification code sent successfully")})
  @PostMapping(value = "/email/send")
  public ApiLocaleResult<?> sendEmail(@Valid @RequestBody CurrentEmailSendDto dto) {
    userCurrentFacade.sendEmail(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Verify email code for current user's email address", operationId = "user:current:email:check")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Email verification completed successfully")})
  @GetMapping(value = "/email/check")
  public ApiLocaleResult<CheckSecretVo> checkEmail(
      @Valid @ParameterObject CurrentEmailCheckDto dto) {
    return ApiLocaleResult.success(userCurrentFacade.checkEmail(dto));
  }

  @Operation(summary = "Update current user's email address", operationId = "user:current:email:update")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Email address updated successfully"),
      @ApiResponse(responseCode = "404", description = "User not found")})
  @PatchMapping(value = "/email")
  public ApiLocaleResult<?> updateEmail(@Valid @RequestBody CurrentEmailUpdateDto dto) {
    userCurrentFacade.updateEmail(dto);
    return ApiLocaleResult.success();
  }

}
