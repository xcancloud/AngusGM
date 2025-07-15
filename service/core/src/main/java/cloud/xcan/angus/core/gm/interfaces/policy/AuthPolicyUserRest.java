package cloud.xcan.angus.core.gm.interfaces.policy;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;

import cloud.xcan.angus.api.gm.user.vo.UserListVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.AuthPolicyUserFacade;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.AuthPolicyAssociatedFindDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.AuthPolicyFindDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.UnAuthPolicyAssociatedFindDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.org.AuthPolicyUserFindDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.AuthPolicyAssociatedVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.AuthPolicyVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.PolicyUnauthVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.experimental.IdKey;
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
import java.util.LinkedHashSet;
import java.util.List;
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


@Tag(name = "AuthPolicyUser", description = "Provides a unified entry for querying the relationship between users and authorization policies")
@Validated
@RestController
@RequestMapping("/api/v1/auth")
public class AuthPolicyUserRest {

  @Resource
  private AuthPolicyUserFacade authPolicyUserFacade;

  @Operation(summary = "Authorize the policy to users", operationId = "auth:policy:user:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/policy/{policyId}/user")
  public ApiLocaleResult<List<IdKey<Long, Object>>> policyUserAdd(
      @Parameter(name = "policyId", description = "Authorization policy id", required = true) @PathVariable("policyId") Long policyId,
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @Parameter(name = "userIds", description = "User ids", required = true)
      @RequestBody LinkedHashSet<Long> userIds) {
    return ApiLocaleResult.success(authPolicyUserFacade.policyUserAdd(policyId, userIds));
  }

  @Operation(summary = "Delete the associated users of authorization policy", operationId = "auth:policy:user:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Deleted successfully")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/policy/{policyId}/user")
  public void policyUserDelete(
      @Parameter(name = "policyId", description = "Policy id", required = true) @PathVariable("policyId") Long policyId,
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @Parameter(name = "userIds", description = "User ids", required = true)
      @RequestParam("userIds") HashSet<Long> userIds) {
    authPolicyUserFacade.policyUserDelete(policyId, userIds);
  }

  @Operation(summary = "Query the user list of authorization policy", operationId = "auth:policy:user:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/policy/{policyId}/user")
  public ApiLocaleResult<PageResult<UserListVo>> policyUserList(
      @Parameter(name = "policyId", description = "Policy id", required = true) @PathVariable("policyId") Long policyId,
      @Valid @ParameterObject AuthPolicyUserFindDto dto) {
    return ApiLocaleResult.success(authPolicyUserFacade.policyUserList(policyId, dto));
  }

  @Operation(summary = "Query the unauthorized user list of policy", operationId = "auth:policy:unauth:user:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/policy/{policyId}/unauth/user")
  public ApiLocaleResult<PageResult<UserListVo>> policyUnauthUserList(
      @Parameter(name = "policyId", description = "Policy id", required = true) @PathVariable("policyId") Long policyId,
      @Valid AuthPolicyUserFindDto dto) {
    return ApiLocaleResult.success(authPolicyUserFacade.policyUnauthUserList(policyId, dto));
  }

  @Operation(summary = "Authorize the policies to user", operationId = "auth:user:policy:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/user/{userId}/policy")
  public ApiLocaleResult<List<IdKey<Long, Object>>> userPolicyAdd(
      @Parameter(name = "userId", description = "User id", required = true) @PathVariable("userId") Long userId,
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @Parameter(name = "policyIds", description = "Policy ids", required = true)
      @RequestBody LinkedHashSet<Long> policyIds) {
    return ApiLocaleResult.success(authPolicyUserFacade.userPolicyAdd(userId, policyIds));
  }

  @Operation(summary = "Delete the authorization policies of user", operationId = "auth:user:policy:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Deleted successfully")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/user/{userId}/policy")
  public void userPolicyDelete(
      @Parameter(name = "userId", description = "User id", required = true) @PathVariable("userId") Long userId,
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @Parameter(name = "policyIds", description = "Policy ids", required = true)
      @RequestParam("policyIds") HashSet<Long> policyIds) {
    authPolicyUserFacade.userPolicyDelete(userId, policyIds);
  }

  @Operation(summary = "Delete the authorization policies of users", operationId = "auth:user:policy:delete:batch")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Deleted successfully")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/user/policy")
  public void userPolicyDeleteBatch(
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @Parameter(name = "userIds", description = "User ids", required = true)
      @RequestParam("userIds") HashSet<Long> userIds,
      @Valid @Size(max = MAX_BATCH_SIZE) @Parameter(name = "policyIds", description = "Policy ids", required = false)
      @RequestParam(value = "policyIds", required = false) HashSet<Long> policyIds) {
    authPolicyUserFacade.userPolicyDeleteBatch(userIds, policyIds);
  }

  @Operation(summary = "Query the authorized policy list of user", operationId = "auth:user:policy:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/user/{userId}/policy")
  public ApiLocaleResult<PageResult<AuthPolicyVo>> userPolicyList(
      @Parameter(name = "userId", description = "User id", required = true) @PathVariable("userId") Long userId,
      @Valid AuthPolicyFindDto dto) {
    return ApiLocaleResult.success(authPolicyUserFacade.userPolicyList(userId, dto));
  }

  @Operation(summary = "Query the all associated authorization policies of user", operationId = "auth:user:policy:association:all")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/user/{userId}/policy/associated")
  public ApiLocaleResult<PageResult<AuthPolicyAssociatedVo>> userAssociatedPolicyList(
      @Parameter(name = "userId", description = "User id", required = true) @PathVariable("userId") Long userId,
      @Valid @ParameterObject AuthPolicyAssociatedFindDto dto) {
    return ApiLocaleResult.success(authPolicyUserFacade.userAssociatedPolicyList(userId, dto));
  }

  @Operation(summary = "Query the unauthorized policy list of user", operationId = "auth:user:unauth:policy:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/user/{userId}/unauth/policy")
  public ApiLocaleResult<PageResult<PolicyUnauthVo>> userUnauthPolicyList(
      @Parameter(name = "userId", description = "User id", required = true) @PathVariable("userId") Long userId,
      @Valid @ParameterObject UnAuthPolicyAssociatedFindDto dto) {
    return ApiLocaleResult.success(authPolicyUserFacade.userUnauthPolicyList(userId, dto));
  }
}
