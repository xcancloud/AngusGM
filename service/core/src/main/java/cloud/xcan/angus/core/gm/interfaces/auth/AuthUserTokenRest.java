package cloud.xcan.angus.core.gm.interfaces.auth;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;

import cloud.xcan.angus.core.gm.interfaces.auth.facade.AuthUserTokenFacade;
import cloud.xcan.angus.core.gm.interfaces.auth.facade.dto.token.UserTokenAddDto;
import cloud.xcan.angus.core.gm.interfaces.auth.facade.vo.token.UserTokenInfoVo;
import cloud.xcan.angus.core.gm.interfaces.auth.facade.vo.token.UserTokenValueVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
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


@Tag(name = "Auth User Token", description = "Manages user access token lifecycle including creation, validation, and revocation")
@Validated
@RestController
@RequestMapping("/api/v1/auth/user")
public class AuthUserTokenRest {

  @Resource
  private AuthUserTokenFacade authUserTokenFacade;

  @Operation(summary = "Create user access token",
      description = "Create custom access token for current user with identical permissions to the associated user's access privileges. "
          + "Used for customizing user authorization duration scenarios",
      operationId = "auth:user:token:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "User access token created successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping(value = "/token")
  public ApiLocaleResult<UserTokenValueVo> add(@Valid @RequestBody UserTokenAddDto dto) {
    return ApiLocaleResult.success(authUserTokenFacade.add(dto));
  }

  @Operation(summary = "Delete user access tokens",
      description = "Note: After deletion, the access tokens will become invalid",
      operationId = "auth:user:token:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "User access tokens deleted successfully")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping(value = "/token")
  public void delete(
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @RequestParam("ids") HashSet<Long> ids) {
    authUserTokenFacade.delete(ids);
  }

  @Operation(summary = "Retrieve user access token value", operationId = "auth:token:value:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User access token value retrieved successfully")})
  @GetMapping("/token/{id}/value")
  public ApiLocaleResult<UserTokenValueVo> value(
      @Parameter(name = "id", description = "User access token identifier", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(authUserTokenFacade.value(id));
  }

  @Operation(summary = "Retrieve current user access tokens", operationId = "auth:user:token:all")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Current user access tokens retrieved successfully")})
  @GetMapping(value = "/token")
  public ApiLocaleResult<List<UserTokenInfoVo>> list() {
    return ApiLocaleResult.success(authUserTokenFacade.list());
  }

}
