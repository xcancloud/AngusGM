package cloud.xcan.angus.core.gm.interfaces.authuser;

import cloud.xcan.angus.core.gm.interfaces.authuser.facade.AuthUserSignFacade;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.dto.sign.AccountQueryDto;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.dto.sign.ForgetPasswordDto;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.dto.sign.RenewDto;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.dto.sign.SignEmailCheckDto;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.dto.sign.SignEmailSendDto;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.dto.sign.SignInDto;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.dto.sign.SignSmsCheckDto;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.dto.sign.SignSmsSendDto;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.dto.sign.SignoutDto;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.dto.sign.SignupDto;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.vo.sign.AccountVo;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.vo.sign.SignVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.spec.experimental.BizConstant.Header;
import cloud.xcan.angus.spec.experimental.IdKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AuthUserSignPub", description = "Governs user lifecycle sign actions, such as: login, registration, password reset, account logout, etc")
@Validated
@RestController
@RequestMapping("/pubapi/v1/auth/user")
public class AuthUserPubRest {

  @Resource
  private AuthUserSignFacade userSignFacade;

  @Operation(summary = "User signup",
      description = "Enable users to securely create account via mobile number or email, "
          + "while ensuring identity verification for authorized access to the system. Note: Duplicate registration is not supported",
      operationId = "auth:user:signup")
  @ResponseStatus(HttpStatus.CREATED)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Signup successfully")})
  @PostMapping("/signup")
  public ApiLocaleResult<IdKey<Long, Object>> signup(
      @Parameter(description = "Current user signup device id") @RequestHeader(value = Header.AUTH_DEVICE_ID, required = false) String deviceId,
      @Valid @RequestBody SignupDto dto) {
    return ApiLocaleResult.success(userSignFacade.signup(deviceId, dto));
  }

  @Operation(summary = "User sign-in",
      description = "Enable authentication through username, mobile number, or email, "
          + "verifying credentials to grant secure access and generate authorization tokens for protected resources",
      operationId = "auth:user:signin")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Sign-in successfully")})
  @PostMapping("/signin")
  public ApiLocaleResult<SignVo> signin(
      @Parameter(description = "Current user sign-in device id") @RequestHeader(value = Header.AUTH_DEVICE_ID, required = false) String deviceId,
      @Valid @RequestBody SignInDto dto) {
    return ApiLocaleResult.success(userSignFacade.signin(deviceId, dto));
  }

  @Operation(summary = "User sign-in by get request",
      description = "Enable authentication through username, mobile number, or email, "
          + "verifying credentials to grant secure access and generate authorization tokens for protected resources. "
          + "Note: The GET request interface is used for convenient debugging or quick login, "
          + "while the POST method is recommended in production environments",
      hidden = true, operationId = "auth:user:signin:get")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Sign-in successfully")})
  @GetMapping("/signin")
  public ApiLocaleResult<SignVo> signinGet(
      @Parameter(description = "Current user sign-in device id") @RequestHeader(value = Header.AUTH_DEVICE_ID, required = false) String deviceId,
      @Valid SignInDto dto) {
    return ApiLocaleResult.success(userSignFacade.signin(deviceId, dto));
  }

  @Operation(summary = "User renew",
      description = "Use OAuth2 refresh token enables clients to securely obtain a new access token "
          + "without requiring user re-authentication, ensuring uninterrupted and secure access to protected resources",
      operationId = "auth:user:renew")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Renew successfully")})
  @PostMapping(value = "/renew")
  public ApiLocaleResult<?> renew(@Valid @RequestBody RenewDto dto) {
    return ApiLocaleResult.success(userSignFacade.renew(dto));
  }

  @Operation(summary = "User renew by get request",
      description = "Use OAuth2 refresh token enables clients to securely obtain a new access token "
          + "without requiring user re-authentication, ensuring uninterrupted and secure access to protected resources. "
          + "Note: The GET request interface is used for convenient debugging or quick retrieval of access tokens, "
          + "while the POST method is recommended in production environments",
      hidden = true, operationId = "auth:user:renew:get")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Renew successfully")})
  @GetMapping(value = "/renew")
  public ApiLocaleResult<SignVo> renewGet(@Valid RenewDto dto) {
    return ApiLocaleResult.success(userSignFacade.renew(dto));
  }

  @Operation(summary = "User sign out",
      description = "Enable securely terminates authenticated sessions "
          + "by invalidating access token, preventing unauthorized access to protected resources",
      operationId = "auth:user:signout")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Signout successfully")})
  @PostMapping(value = "/signout")
  public ApiLocaleResult<?> signout(@Valid @RequestBody SignoutDto dto) {
    userSignFacade.signout(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "User sign out by get request",
      description = "Enable securely terminates authenticated sessions by invalidating access token, "
          + "preventing unauthorized access to protected resources. "
          + "Note: The GET request interface is used for convenient debugging or quick logout system, "
          + "while the POST method is recommended in production environments",
      hidden = true, operationId = "auth:user:signout:get")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Signout successfully")})
  @GetMapping(value = "/signout")
  public ApiLocaleResult<?> signoutGet(@Valid SignoutDto dto) {
    userSignFacade.signout(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Query registered accounts",
      description = "Retrieve registered tenant accounts to choose and access a specific tenant during the login process. "
          + "Note: Only return accounts that match both the account and password",
      operationId = "auth:user:signin:account")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping(value = "/signin/account")
  public ApiLocaleResult<List<AccountVo>> tenantAccount(@Valid AccountQueryDto dto) {
    return ApiLocaleResult.success(userSignFacade.tenantAccount(dto));
  }

  @Operation(summary = "User reset password after forget",
      description = "Enable users to securely reset their password via a verification process, "
          + "ensuring account recovery while preventing unauthorized access",
      operationId = "auth:user:password:forget")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Updated successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @PatchMapping(value = "/password/forget")
  public ApiLocaleResult<?> forgetPassword(@Valid @RequestBody ForgetPasswordDto dto) {
    userSignFacade.forgetPassword(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Send the sms verification code",
      description = "Send the sms verification code of user sign, such as: sms signup, sign-in, password recovery, etc",
      operationId = "auth:user:sign:sms:send")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Send successfully")})
  @PostMapping(value = "/signsms/send")
  public ApiLocaleResult<?> sendSignSms(@Valid @RequestBody SignSmsSendDto dto) {
    userSignFacade.sendSignSms(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Check the sms verification code of user sign",
      description = "Check the sms verification code of user sign, such as: sms signup, sign-in, password recovery, etc",
      operationId = "auth:user:sign:sms:check")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Check successfully")})
  @GetMapping(value = "/signsms/check")
  public ApiLocaleResult<List<AccountVo>> checkSignSms(@Valid SignSmsCheckDto dto) {
    return ApiLocaleResult.success(userSignFacade.checkSignSms(dto));
  }

  @Operation(summary = "Send the email verification code of user sign",
      description = "Send the email verification code of user sign, such as: email signup, sign-in, password recovery, etc",
      operationId = "auth:user:sign:email:send")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Send successfully")})
  @PostMapping(value = "/signemail/send")
  public ApiLocaleResult<?> sendSignEmail(@Valid @RequestBody SignEmailSendDto dto) {
    userSignFacade.sendSignEmail(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Check the email verification code of user sign",
      description = "Check the email verification code of user sign, such as: email signup, sign-in, password recovery, etc",
      operationId = "auth:user:sign:email:check")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Check successfully")})
  @GetMapping(value = "/signemail/check")
  public ApiLocaleResult<List<AccountVo>> checkSignEmail(@Valid SignEmailCheckDto dto) {
    return ApiLocaleResult.success(userSignFacade.checkSignEmail(dto));
  }

}
