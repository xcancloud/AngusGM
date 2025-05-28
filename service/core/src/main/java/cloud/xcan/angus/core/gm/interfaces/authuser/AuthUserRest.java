package cloud.xcan.angus.core.gm.interfaces.authuser;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;

import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.AppVo;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.AuthUserFacade;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.dto.AuthUserPasswordCheckDto;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.dto.AuthUserPasswordUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.dto.AuthUserRealNameUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.dto.CurrentAuthUserPasswordCheckDto;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.dto.CurrentAuthUserPasswordInitDto;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.dto.CurrentAuthUserPasswordUpdateDto;
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

@Tag(name = "AuthUser", description = "Administers OAuth2 user identities and authorization grants.")
@Validated
@RestController
@RequestMapping("/api/v1/auth/user")
public class AuthUserRest {

  @Resource
  private AuthUserFacade userFacade;

  @Operation(summary = "Update the password of user.", operationId = "auth:user:password:update")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Processed successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @PatchMapping(value = "/password")
  public ApiLocaleResult<?> updatePassword(@Valid @RequestBody AuthUserPasswordUpdateDto dto) {
    userFacade.updateCurrentPassword(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Check whether the user password is correct.", operationId = "auth:user:password:check")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Check successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping(value = "/password/check")
  public ApiLocaleResult<?> checkPassword(@Valid AuthUserPasswordCheckDto dto) {
    userFacade.checkPassword(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary =
      "Delete oauth2 authorization users. Note: After deleting the authorized user, "
          + "they will not be able to log in to the system.", operationId = "auth:user:delete")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Deleted successfully")})
  @DeleteMapping
  public void delete(
      @Parameter(name = "ids", description = "User ids", required = true)
      @Valid @Size(max = MAX_BATCH_SIZE) @RequestParam("ids") HashSet<Long> ids) {
    userFacade.delete(ids);
  }

  @Operation(summary = "Modify the user's real-name status after the tenant real name authentication.",
      operationId = "auth:user:realname:update")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Updated successfully")
  })
  @PatchMapping("/realname")
  public ApiLocaleResult<?> realname(@Valid @RequestBody AuthUserRealNameUpdateDto dto) {
    userFacade.realname(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Update the password of current user.", operationId = "auth:user:password:update:current")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Processed successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @PatchMapping(value = "/password/current")
  public ApiLocaleResult<?> updateCurrentPassword(
      @Valid @RequestBody CurrentAuthUserPasswordUpdateDto dto) {
    userFacade.updateCurrentPassword(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Check the password of current user.", operationId = "auth:user:password:check:current")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Processed successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping(value = "/password/check/current")
  public ApiLocaleResult<?> checkCurrentPassword(@Valid CurrentAuthUserPasswordCheckDto dto) {
    userFacade.checkCurrentPassword(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Initialize the password of current user.", operationId = "auth:user:password:init:current")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Processed successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @PatchMapping(value = "/password/init/current")
  public ApiLocaleResult<?> initCurrentPassword(
      @Valid @RequestBody CurrentAuthUserPasswordInitDto dto) {
    userFacade.initCurrentPassword(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Query the all authorized application information of user.", operationId = "auth:user:app:all")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/{userId}/app")
  public ApiLocaleResult<List<AppVo>> userAppList(
      @Parameter(name = "userId", description = "User id", required = true) @PathVariable("userId") Long userId) {
    return ApiLocaleResult.success(userFacade.userAppList(userId));
  }

  @Operation(summary = "Query all authorized application functions list of user.", operationId = "auth:user:app:func:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping(value = "/{userId}/app/{appIdOrCode}/func")
  public ApiLocaleResult<AuthAppVo> userAppFuncList(
      @Parameter(name = "userId", description = "User id", required = true) @PathVariable("userId") Long userId,
      @Parameter(name = "appIdOrCode", description = "Application id or code", required = true) @PathVariable("appIdOrCode") String appIdOrCode,
      @Parameter(name = "joinApi", description = "Association query function api flag", required = false) @RequestParam(value = "joinApi", required = false) Boolean joinApi,
      @Parameter(name = "joinTag", description = "Association query function tag flag", required = false) @RequestParam(value = "joinTag", required = false) Boolean joinTag,
      @Parameter(name = "onlyEnabled", description = "Only return enabled function, default true", required = false) @RequestParam(value = "onlyEnabled", required = false) Boolean onlyEnabled) {
    return ApiLocaleResult.success(userFacade.userAppFuncList(userId, appIdOrCode, joinApi,
        joinTag, onlyEnabled));
  }

  @Operation(summary = "Query all authorized application functions tree of user.", operationId = "auth:user:app:func:tree")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping(value = "/{userId}/app/{appIdOrCode}/func/tree")
  public ApiLocaleResult<AuthAppTreeVo> appFuncTree(
      @Parameter(name = "userId", description = "User id", required = true) @PathVariable("userId") Long userId,
      @Parameter(name = "appIdOrCode", description = "Application id or code", required = true) @PathVariable("appIdOrCode") String appIdOrCode,
      @Parameter(name = "joinApi", description = "Association query function api flag", required = false) @RequestParam(value = "joinApi", required = false) Boolean joinApi,
      @Parameter(name = "joinTag", description = "Association query function tag flag", required = false) @RequestParam(value = "joinTag", required = false) Boolean joinTag,
      @Parameter(name = "onlyEnabled", description = "Only return enabled function, default true", required = false) @RequestParam(value = "onlyEnabled", required = false) Boolean onlyEnabled) {
    return ApiLocaleResult.success(userFacade.appFuncTree(userId, appIdOrCode, joinApi,
        joinTag, onlyEnabled));
  }

}
