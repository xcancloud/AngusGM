package cloud.xcan.angus.core.gm.interfaces.authuser;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;

import cloud.xcan.angus.core.gm.interfaces.authuser.facade.AuthUserTokenFacade;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.dto.token.UserTokenAddDto;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.vo.token.UserTokenInfoVo;
import cloud.xcan.angus.core.gm.interfaces.authuser.facade.vo.token.UserTokenValueVo;
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


@Tag(name = "AuthUserToken", description = "Controls user token issuance, validation, and revocation.")
@Validated
@RestController
@RequestMapping("/api/v1/auth/user")
public class AuthUserTokenRest {

  @Resource
  private AuthUserTokenFacade authUserTokenFacade;

  @Operation(summary =
      "Add token of current user. Used for customizing user authorization duration scenarios "
          + "while maintaining permissions identical to the associated user's access privileges.", operationId = "auth:user:token:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping(value = "/token")
  public ApiLocaleResult<UserTokenValueVo> add(@Valid @RequestBody UserTokenAddDto dto) {
    return ApiLocaleResult.success(authUserTokenFacade.add(dto));
  }

  @Operation(summary = "Delete the tokens of current user. Note: After deletion, "
      + "the access token will become invalid.", operationId = "auth:user:token:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Deleted successfully")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping(value = "/token")
  public void delete(
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @RequestParam("ids") HashSet<Long> ids) {
    authUserTokenFacade.delete(ids);
  }

  @Operation(summary = "Retrieve the value of the specified token associated with the current user.", operationId = "auth:token:value:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/token/{id}/value")
  public ApiLocaleResult<UserTokenValueVo> value(
      @Parameter(name = "id", description = "User token id", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(authUserTokenFacade.value(id));
  }

  @Operation(summary = "Query the all tokens of current user.", operationId = "auth:user:token:all")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping(value = "/token")
  public ApiLocaleResult<List<UserTokenInfoVo>> list() {
    return ApiLocaleResult.success(authUserTokenFacade.list());
  }

}
