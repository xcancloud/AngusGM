package cloud.xcan.angus.core.gm.interfaces.authentication;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;

import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.AppVo;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.AuthUserFacade;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.AuthUserPasswordCheckDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.AuthUserPasswordUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.AuthUserRealNameUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.CurrentAuthUserPasswordCheckDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.CurrentAuthUserPasswordInitDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.CurrentAuthUserPasswordUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.func.AuthAppTreeVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.func.AuthAppVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth User", description = "Manages OAuth2 user identities, authorization permissions, and password operations")
@Validated
@RestController
@RequestMapping("/api/v1/auth/user")
public class AuthUserRest {

  @Resource
  private AuthUserFacade userFacade;

  @Operation(summary = "Update user password", operationId = "auth:user:password:update")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User password updated successfully"),
      @ApiResponse(responseCode = "404", description = "User not found")})
  @PatchMapping(value = "/password")
  public ApiLocaleResult<?> updatePassword(@Valid @RequestBody AuthUserPasswordUpdateDto dto) {
    userFacade.updateCurrentPassword(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Verify user password", operationId = "auth:user:password:check")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Password verification completed successfully"),
      @ApiResponse(responseCode = "404", description = "User not found")})
  @GetMapping(value = "/password/check")
  public ApiLocaleResult<?> checkPassword(@Valid AuthUserPasswordCheckDto dto) {
    userFacade.checkPassword(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Delete OAuth2 authorized users",
      description = "Note: After deleting the authorized user, they will not be able to log in to the system",
      operationId = "auth:user:delete")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Users deleted successfully")})
  @DeleteMapping
  public void delete(
      @Parameter(name = "ids", description = "User identifiers", required = true)
      @Valid @Size(max = MAX_BATCH_SIZE) @RequestParam("ids") HashSet<Long> ids) {
    userFacade.delete(ids);
  }

  @Operation(summary = "Update user real-name verification status",
      description = "Update user real-name verification status after tenant real-name authentication",
      operationId = "auth:user:realname:update")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Real-name status updated successfully")
  })
  @PatchMapping("/realname")
  public ApiLocaleResult<?> realname(@Valid @RequestBody AuthUserRealNameUpdateDto dto) {
    userFacade.realname(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Update current user password", operationId = "auth:user:password:update:current")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Current user password updated successfully"),
      @ApiResponse(responseCode = "404", description = "User not found")})
  @PatchMapping(value = "/password/current")
  public ApiLocaleResult<?> updateCurrentPassword(
      @Valid @RequestBody CurrentAuthUserPasswordUpdateDto dto) {
    userFacade.updateCurrentPassword(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Verify current user password", operationId = "auth:user:password:check:current")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Current user password verification completed successfully"),
      @ApiResponse(responseCode = "404", description = "User not found")})
  @GetMapping(value = "/password/check/current")
  public ApiLocaleResult<?> checkCurrentPassword(@Valid CurrentAuthUserPasswordCheckDto dto) {
    userFacade.checkCurrentPassword(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Initialize current user password", operationId = "auth:user:password:init:current")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Current user password initialized successfully"),
      @ApiResponse(responseCode = "404", description = "User not found")})
  @PatchMapping(value = "/password/init/current")
  public ApiLocaleResult<?> initCurrentPassword(
      @Valid @RequestBody CurrentAuthUserPasswordInitDto dto) {
    userFacade.initCurrentPassword(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Retrieve user authorized applications", operationId = "auth:user:app:all")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User applications retrieved successfully")})
  @GetMapping("/{userId}/app")
  public ApiLocaleResult<List<AppVo>> userAppList(
      @Parameter(name = "userId", description = "User identifier", required = true) @PathVariable("userId") Long userId) {
    return ApiLocaleResult.success(userFacade.userAppList(userId));
  }

  @Operation(summary = "Retrieve user authorized application functions", operationId = "auth:user:app:func:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User application functions retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Application not found")})
  @GetMapping(value = "/{userId}/app/{appIdOrCode}/func")
  public ApiLocaleResult<AuthAppVo> userAppFuncList(
      @Parameter(name = "userId", description = "User identifier", required = true) @PathVariable("userId") Long userId,
      @Parameter(name = "appIdOrCode", description = "Application identifier or code", required = true) @PathVariable("appIdOrCode") String appIdOrCode,
      @Parameter(name = "joinApi", description = "Include API information in function query", required = false) @RequestParam(value = "joinApi", required = false) Boolean joinApi,
      @Parameter(name = "onlyEnabled", description = "Return only enabled functions, default true", required = false) @RequestParam(value = "onlyEnabled", required = false) Boolean onlyEnabled) {
    return ApiLocaleResult.success(userFacade.userAppFuncList(userId, appIdOrCode, joinApi, onlyEnabled));
  }

  @Operation(summary = "Retrieve user authorized application function tree", operationId = "auth:user:app:func:tree")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User application function tree retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Application not found")})
  @GetMapping(value = "/{userId}/app/{appIdOrCode}/func/tree")
  public ApiLocaleResult<AuthAppTreeVo> appFuncTree(
      @Parameter(name = "userId", description = "User identifier", required = true) @PathVariable("userId") Long userId,
      @Parameter(name = "appIdOrCode", description = "Application identifier or code", required = true) @PathVariable("appIdOrCode") String appIdOrCode,
      @Parameter(name = "joinApi", description = "Include API information in function query", required = false) @RequestParam(value = "joinApi", required = false) Boolean joinApi,
      @Parameter(name = "onlyEnabled", description = "Return only enabled functions, default true", required = false) @RequestParam(value = "onlyEnabled", required = false) Boolean onlyEnabled) {
    return ApiLocaleResult.success(userFacade.appFuncTree(userId, appIdOrCode, joinApi, onlyEnabled));
  }

}
