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


@Tag(name = "AuthPolicyGroup", description = "Provides a unified entry for querying the relationship between groups and authorization policies.")
@Validated
@RestController
@RequestMapping("/api/v1/auth")
public class AuthPolicyGroupRest {

  @Resource
  private AuthPolicyGroupFacade authPolicyGroupFacade;

  @Operation(description = "Authorize the policy to groups.", operationId = "auth:policy:group:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/policy/{policyId}/group")
  public ApiLocaleResult<List<IdKey<Long, Object>>> policyGroupAdd(
      @Parameter(name = "policyId", description = "Authorization policy id", required = true) @PathVariable("policyId") Long policyId,
      @Valid @Size(max = MAX_BATCH_SIZE) @Parameter(name = "groupIds", description = "Group ids", required = true)
      @RequestBody LinkedHashSet<Long> groupIds) {
    return ApiLocaleResult.success(authPolicyGroupFacade.policyGroupAdd(policyId, groupIds));
  }

  @Operation(description = "Delete the associated groups of authorization policy.", operationId = "auth:policy:group:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Deleted successfully")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/policy/{policyId}/group")
  public void policyGroupDelete(
      @Parameter(name = "policyId", description = "Policy id", required = true) @PathVariable("policyId") Long policyId,
      @Valid @Size(max = MAX_BATCH_SIZE) @Parameter(name = "groupIds", description = "Group ids", required = true)
      @RequestParam("groupIds") HashSet<Long> groupIds) {
    authPolicyGroupFacade.policyGroupDelete(policyId, groupIds);
  }

  @Operation(description = "Query the group list of authorization policy.", operationId = "auth:policy:group:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/policy/{policyId}/group")
  public ApiLocaleResult<PageResult<GroupListVo>> policyGroupList(
      @Parameter(name = "policyId", description = "Policy id", required = true) @PathVariable("policyId") Long policyId,
      @Valid @ParameterObject AuthPolicyGroupFindDto dto) {
    return ApiLocaleResult.success(authPolicyGroupFacade.policyGroupList(policyId, dto));
  }

  @Operation(description = "Query the unauthorized group list of authorization policy.", operationId = "auth:policy:unauth:group:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/policy/{policyId}/unauth/group")
  public ApiLocaleResult<PageResult<GroupListVo>> policyUnauthGroupList(
      @Parameter(name = "policyId", description = "Policy id", required = true) @PathVariable("policyId") Long policyId,
      @Valid AuthPolicyGroupFindDto dto) {
    return ApiLocaleResult.success(authPolicyGroupFacade.policyUnauthGroupList(policyId, dto));
  }

  @Operation(description = "Authorize the policies to group.", operationId = "auth:group:policy:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/group/{groupId}/policy")
  public ApiLocaleResult<List<IdKey<Long, Object>>> groupPolicyAdd(
      @Parameter(name = "groupId", description = "Group id", required = true) @PathVariable("groupId") Long groupId,
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @Parameter(name = "policyIds", description = "Policy ids", required = true)
      @RequestBody LinkedHashSet<Long> policyIds) {
    return ApiLocaleResult.success(authPolicyGroupFacade.groupPolicyAdd(groupId, policyIds));
  }

  @Operation(description = "Delete the authorization policies of group.", operationId = "auth:group:policy:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Deleted successfully")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/group/{groupId}/policy")
  public void groupPolicyDelete(
      @Parameter(name = "groupId", description = "Group id", required = true) @PathVariable("groupId") Long groupId,
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @Parameter(name = "policyIds", description = "Policy ids", required = true)
      @RequestParam("policyIds") HashSet<Long> policyIds) {
    authPolicyGroupFacade.groupPolicyDelete(groupId, policyIds);
  }

  @Operation(description = "Delete the authorization policies of groups.", operationId = "auth:group:policy:delete:batch")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Deleted successfully")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/group/policy")
  public void groupPolicyDeleteBatch(
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @Parameter(name = "groupIds", description = "Group ids", required = true)
      @RequestParam("groupIds") HashSet<Long> groupIds,
      @Valid @Size(max = MAX_BATCH_SIZE) @Parameter(name = "policyIds", description = "Policy ids", required = false)
      @RequestParam(value = "policyIds", required = false) HashSet<Long> policyIds) {
    authPolicyGroupFacade.groupPolicyDeleteBatch(groupIds, policyIds);
  }

  @Operation(description = "Query the authorized policy list of group.", operationId = "auth:group:policy:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/group/{groupId}/policy")
  public ApiLocaleResult<PageResult<AuthPolicyVo>> groupPolicyList(
      @Parameter(name = "groupId", description = "Group id", required = true) @PathVariable("groupId") Long groupId,
      @Valid AuthPolicyFindDto dto) {
    return ApiLocaleResult.success(authPolicyGroupFacade.groupPolicyList(groupId, dto));
  }

  @Operation(description = "Query the unauthorized policy list of group.", operationId = "auth:group:unauth:policy:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/group/{groupId}/unauth/policy")
  public ApiLocaleResult<PageResult<PolicyUnauthVo>> groupUnauthPolicyList(
      @Parameter(name = "groupId", description = "Group id", required = true) @PathVariable("groupId") Long groupId,
      @Valid UnAuthPolicyFindDto dto) {
    return ApiLocaleResult.success(authPolicyGroupFacade.groupUnauthPolicyList(groupId, dto));
  }

}
