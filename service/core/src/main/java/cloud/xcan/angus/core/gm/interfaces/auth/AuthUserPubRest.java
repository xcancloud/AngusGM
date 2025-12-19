package cloud.xcan.angus.core.gm.interfaces.auth;

import cloud.xcan.angus.core.gm.interfaces.auth.facade.AuthUserSignFacade;
import cloud.xcan.angus.core.gm.interfaces.auth.facade.dto.sign.AccountQueryDto;
import cloud.xcan.angus.core.gm.interfaces.auth.facade.dto.sign.ForgetPasswordDto;
import cloud.xcan.angus.core.gm.interfaces.auth.facade.dto.sign.RenewDto;
import cloud.xcan.angus.core.gm.interfaces.auth.facade.dto.sign.SignEmailCheckDto;
import cloud.xcan.angus.core.gm.interfaces.auth.facade.dto.sign.SignEmailSendDto;
import cloud.xcan.angus.core.gm.interfaces.auth.facade.dto.sign.SignInDto;
import cloud.xcan.angus.core.gm.interfaces.auth.facade.dto.sign.SignSmsCheckDto;
import cloud.xcan.angus.core.gm.interfaces.auth.facade.dto.sign.SignSmsSendDto;
import cloud.xcan.angus.core.gm.interfaces.auth.facade.dto.sign.SignoutDto;
import cloud.xcan.angus.core.gm.interfaces.auth.facade.dto.sign.SignupDto;
import cloud.xcan.angus.core.gm.interfaces.auth.facade.vo.sign.AccountVo;
import cloud.xcan.angus.core.gm.interfaces.auth.facade.vo.sign.SignVo;
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

@Tag(name = "Auth User - Public", description = "Public API for user authentication lifecycle operations including login, registration, password reset, and logout")
@Validated
@RestController
@RequestMapping("/pubapi/v1/auth/user")
public class AuthUserPubRest {

  @Resource
  private AuthUserSignFacade userSignFacade;

  @Operation(summary = "Register new user account",
      description = "Enable users to securely create account via mobile number or email, "
          + "while ensuring identity verification for authorized access to the system. Note: Duplicate registration is not supported",
      operationId = "auth:user:signup")
  @ResponseStatus(HttpStatus.CREATED)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "User account created successfully")})
  @PostMapping("/signup")
  public ApiLocaleResult<IdKey<Long, Object>> signup(
      @Parameter(description = "User device identifier for registration") @RequestHeader(value = Header.AUTH_DEVICE_ID, required = false) String deviceId,
      @Valid @RequestBody SignupDto dto) {
    return ApiLocaleResult.success(userSignFacade.signup(deviceId, dto));
  }

  @Operation(summary = "Authenticate user account",
      description = "Enable authentication through username, mobile number, or email, "
          + "verifying credentials to grant secure access and generate authorization tokens for protected resources",
      operationId = "auth:user:signin")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User authenticated successfully")})
  @PostMapping("/signin")
  public ApiLocaleResult<SignVo> signin(
      @Parameter(description = "User device identifier for authentication") @RequestHeader(value = Header.AUTH_DEVICE_ID, required = false) String deviceId,
      @Valid @RequestBody SignInDto dto) {
    return ApiLocaleResult.success(userSignFacade.signin(deviceId, dto));
  }

  @Operation(summary = "Authenticate user account via GET request",
      description = "Enable authentication through username, mobile number, or email, "
          + "verifying credentials to grant secure access and generate authorization tokens for protected resources. "
          + "Note: The GET request interface is used for convenient debugging or quick login, "
          + "while the POST method is recommended in production environments",
      hidden = true, operationId = "auth:user:signin:get")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User authenticated successfully")})
  @GetMapping("/signin")
  public ApiLocaleResult<SignVo> signinGet(
      @Parameter(description = "User device identifier for authentication") @RequestHeader(value = Header.AUTH_DEVICE_ID, required = false) String deviceId,
      @Valid SignInDto dto) {
    return ApiLocaleResult.success(userSignFacade.signin(deviceId, dto));
  }

  @Operation(summary = "Refresh access token",
      description =
          "Use OAuth2 refresh token enables clients to securely obtain a new access token "
              + "without requiring user re-authentication, ensuring uninterrupted and secure access to protected resources",
      operationId = "auth:user:renew")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Access token refreshed successfully")})
  @PostMapping(value = "/renew")
  public ApiLocaleResult<?> renew(@Valid @RequestBody RenewDto dto) {
    return ApiLocaleResult.success(userSignFacade.renew(dto));
  }

  @Operation(summary = "Refresh access token via GET request",
      description =
          "Use OAuth2 refresh token enables clients to securely obtain a new access token "
              + "without requiring user re-authentication, ensuring uninterrupted and secure access to protected resources. "
              + "Note: The GET request interface is used for convenient debugging or quick retrieval of access tokens, "
              + "while the POST method is recommended in production environments",
      hidden = true, operationId = "auth:user:renew:get")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Access token refreshed successfully")})
  @GetMapping(value = "/renew")
  public ApiLocaleResult<SignVo> renewGet(@Valid RenewDto dto) {
    return ApiLocaleResult.success(userSignFacade.renew(dto));
  }

  @Operation(summary = "Terminate user session",
      description = "Enable securely terminates authenticated sessions "
          + "by invalidating access token, preventing unauthorized access to protected resources",
      operationId = "auth:user:signout")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User session terminated successfully")})
  @PostMapping(value = "/signout")
  public ApiLocaleResult<?> signout(@Valid @RequestBody SignoutDto dto) {
    userSignFacade.signout(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Terminate user session via GET request",
      description =
          "Enable securely terminates authenticated sessions by invalidating access token, "
              + "preventing unauthorized access to protected resources. "
              + "Note: The GET request interface is used for convenient debugging or quick logout system, "
              + "while the POST method is recommended in production environments",
      hidden = true, operationId = "auth:user:signout:get")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User session terminated successfully")})
  @GetMapping(value = "/signout")
  public ApiLocaleResult<?> signoutGet(@Valid SignoutDto dto) {
    userSignFacade.signout(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Retrieve registered tenant accounts",
      description =
          "Retrieve registered tenant accounts to choose and access a specific tenant during the login process. "
              + "Note: Only return accounts that match both the account and password",
      operationId = "auth:user:signin:account")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Tenant accounts retrieved successfully")})
  @GetMapping(value = "/signin/account")
  public ApiLocaleResult<List<AccountVo>> tenantAccount(@Valid AccountQueryDto dto) {
    return ApiLocaleResult.success(userSignFacade.tenantAccount(dto));
  }

  @Operation(summary = "Reset user password",
      description = "Enable users to securely reset their password via a verification process, "
          + "ensuring account recovery while preventing unauthorized access",
      operationId = "auth:user:password:forget")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Password reset successfully"),
      @ApiResponse(responseCode = "404", description = "User not found")})
  @PatchMapping(value = "/password/forget")
  public ApiLocaleResult<?> forgetPassword(@Valid @RequestBody ForgetPasswordDto dto) {
    userSignFacade.forgetPassword(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Send SMS verification code",
      description = "Send SMS verification code for user authentication operations, such as: SMS registration, sign-in, password recovery, etc",
      operationId = "auth:user:sign:sms:send")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "SMS verification code sent successfully")})
  @PostMapping(value = "/signsms/send")
  public ApiLocaleResult<?> sendSignSms(@Valid @RequestBody SignSmsSendDto dto) {
    userSignFacade.sendSignSms(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Verify SMS verification code",
      description = "Verify SMS verification code for user authentication operations, such as: SMS registration, sign-in, password recovery, etc",
      operationId = "auth:user:sign:sms:check")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "SMS verification code verified successfully")})
  @GetMapping(value = "/signsms/check")
  public ApiLocaleResult<List<AccountVo>> checkSignSms(@Valid SignSmsCheckDto dto) {
    return ApiLocaleResult.success(userSignFacade.checkSignSms(dto));
  }

  @Operation(summary = "Send email verification code",
      description = "Send email verification code for user authentication operations, such as: email registration, sign-in, password recovery, etc",
      operationId = "auth:user:sign:email:send")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Email verification code sent successfully")})
  @PostMapping(value = "/signemail/send")
  public ApiLocaleResult<?> sendSignEmail(@Valid @RequestBody SignEmailSendDto dto) {
    userSignFacade.sendSignEmail(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Verify email verification code",
      description = "Verify email verification code for user authentication operations, such as: email registration, sign-in, password recovery, etc",
      operationId = "auth:user:sign:email:check")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Email verification code verified successfully")})
  @GetMapping(value = "/signemail/check")
  public ApiLocaleResult<List<AccountVo>> checkSignEmail(@Valid SignEmailCheckDto dto) {
    return ApiLocaleResult.success(userSignFacade.checkSignEmail(dto));
  }

}
