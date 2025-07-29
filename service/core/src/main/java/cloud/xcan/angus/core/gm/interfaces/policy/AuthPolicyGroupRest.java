package cloud.xcan.angus.core.gm.interfaces.policy;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;

import cloud.xcan.angus.api.gm.group.vo.GroupListVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.AuthPolicyGroupFacade;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.AuthPolicyFindDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.UnAuthPolicyFindDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.org.AuthPolicyGroupFindDto;
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


@Tag(name = "Auth Policy Group", description = "REST API endpoints for managing authorization policy and group relationships")
@Validated
@RestController
@RequestMapping("/api/v1/auth")
public class AuthPolicyGroupRest {

  @Resource
  private AuthPolicyGroupFacade authPolicyGroupFacade;

  @Operation(summary = "Assign authorization policy to multiple groups", operationId = "auth:policy:group:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Policy assigned to groups successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/policy/{policyId}/group")
  public ApiLocaleResult<List<IdKey<Long, Object>>> policyGroupAdd(
      @Parameter(name = "policyId", description = "Authorization policy identifier", required = true) @PathVariable("policyId") Long policyId,
      @Valid @Size(max = MAX_BATCH_SIZE) @Parameter(name = "groupIds", description = "Group identifiers to assign policy to", required = true)
      @RequestBody LinkedHashSet<Long> groupIds) {
    return ApiLocaleResult.success(authPolicyGroupFacade.policyGroupAdd(policyId, groupIds));
  }

  @Operation(summary = "Remove authorization policy from groups", operationId = "auth:policy:group:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Policy removed from groups successfully")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/policy/{policyId}/group")
  public void policyGroupDelete(
      @Parameter(name = "policyId", description = "Authorization policy identifier", required = true) @PathVariable("policyId") Long policyId,
      @Valid @Size(max = MAX_BATCH_SIZE) @Parameter(name = "groupIds", description = "Group identifiers to remove policy from", required = true)
      @RequestParam("groupIds") HashSet<Long> groupIds) {
    authPolicyGroupFacade.policyGroupDelete(policyId, groupIds);
  }

  @Operation(summary = "Retrieve groups assigned to an authorization policy", operationId = "auth:policy:group:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Group list retrieved successfully")})
  @GetMapping("/policy/{policyId}/group")
  public ApiLocaleResult<PageResult<GroupListVo>> policyGroupList(
      @Parameter(name = "policyId", description = "Authorization policy identifier", required = true) @PathVariable("policyId") Long policyId,
      @Valid @ParameterObject AuthPolicyGroupFindDto dto) {
    return ApiLocaleResult.success(authPolicyGroupFacade.policyGroupList(policyId, dto));
  }

  @Operation(summary = "Retrieve groups not assigned to an authorization policy", operationId = "auth:policy:unauth:group:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Unauthorized group list retrieved successfully")})
  @GetMapping("/policy/{policyId}/unauth/group")
  public ApiLocaleResult<PageResult<GroupListVo>> policyUnauthGroupList(
      @Parameter(name = "policyId", description = "Authorization policy identifier", required = true) @PathVariable("policyId") Long policyId,
      @Valid @ParameterObject AuthPolicyGroupFindDto dto) {
    return ApiLocaleResult.success(authPolicyGroupFacade.policyUnauthGroupList(policyId, dto));
  }

  @Operation(summary = "Assign multiple authorization policies to a group", operationId = "auth:group:policy:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Policies assigned to group successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/group/{groupId}/policy")
  public ApiLocaleResult<List<IdKey<Long, Object>>> groupPolicyAdd(
      @Parameter(name = "groupId", description = "Group identifier", required = true) @PathVariable("groupId") Long groupId,
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @Parameter(name = "policyIds", description = "Authorization policy identifiers to assign", required = true)
      @RequestBody LinkedHashSet<Long> policyIds) {
    return ApiLocaleResult.success(authPolicyGroupFacade.groupPolicyAdd(groupId, policyIds));
  }

  @Operation(summary = "Remove authorization policies from a group", operationId = "auth:group:policy:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Policies removed from group successfully")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/group/{groupId}/policy")
  public void groupPolicyDelete(
      @Parameter(name = "groupId", description = "Group identifier", required = true) @PathVariable("groupId") Long groupId,
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @Parameter(name = "policyIds", description = "Authorization policy identifiers to remove", required = true)
      @RequestParam("policyIds") HashSet<Long> policyIds) {
    authPolicyGroupFacade.groupPolicyDelete(groupId, policyIds);
  }

  @Operation(summary = "Remove authorization policies from multiple groups", operationId = "auth:group:policy:delete:batch")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Policies removed from groups successfully")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/group/policy")
  public void groupPolicyDeleteBatch(
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @Parameter(name = "groupIds", description = "Group identifiers", required = true)
      @RequestParam("groupIds") HashSet<Long> groupIds,
      @Valid @Size(max = MAX_BATCH_SIZE) @Parameter(name = "policyIds", description = "Authorization policy identifiers to remove (optional)", required = false)
      @RequestParam(value = "policyIds", required = false) HashSet<Long> policyIds) {
    authPolicyGroupFacade.groupPolicyDeleteBatch(groupIds, policyIds);
  }

  @Operation(summary = "Retrieve authorization policies assigned to a group", operationId = "auth:group:policy:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Group policies retrieved successfully")})
  @GetMapping("/group/{groupId}/policy")
  public ApiLocaleResult<PageResult<AuthPolicyVo>> groupPolicyList(
      @Parameter(name = "groupId", description = "Group identifier", required = true) @PathVariable("groupId") Long groupId,
      @Valid @ParameterObject AuthPolicyFindDto dto) {
    return ApiLocaleResult.success(authPolicyGroupFacade.groupPolicyList(groupId, dto));
  }

  @Operation(summary = "Retrieve authorization policies not assigned to a group", operationId = "auth:group:unauth:policy:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Unauthorized policies retrieved successfully")})
  @GetMapping("/group/{groupId}/unauth/policy")
  public ApiLocaleResult<PageResult<PolicyUnauthVo>> groupUnauthPolicyList(
      @Parameter(name = "groupId", description = "Group identifier", required = true) @PathVariable("groupId") Long groupId,
      @Valid @ParameterObject UnAuthPolicyFindDto dto) {
    return ApiLocaleResult.success(authPolicyGroupFacade.groupUnauthPolicyList(groupId, dto));
  }

}
