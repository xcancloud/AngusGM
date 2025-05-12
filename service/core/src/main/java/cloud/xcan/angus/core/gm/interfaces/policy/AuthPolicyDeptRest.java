package cloud.xcan.angus.core.gm.interfaces.policy;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;

import cloud.xcan.angus.api.gm.dept.vo.DeptListVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.AuthPolicyDeptFacade;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.AuthPolicyFindDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.UnAuthPolicyFindDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.org.AuthPolicyDeptFindDto;
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


@Tag(name = "AuthPolicyDept", description = "Provides a unified entry for querying the relationship between departments and authorization policies.")
@Validated
@RestController
@RequestMapping("/api/v1/auth")
public class AuthPolicyDeptRest {

  @Resource
  private AuthPolicyDeptFacade authPolicyDeptFacade;

  @Operation(description = "Authorize the policy to departments.", operationId = "auth:policy:dept:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/policy/{policyId}/dept")
  public ApiLocaleResult<List<IdKey<Long, Object>>> policyDeptAdd(
      @Parameter(name = "policyId", description = "Authorization policy id", required = true) @PathVariable("policyId") Long policyId,
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @Parameter(name = "deptIds", description = "Department ids", required = true)
      @RequestBody LinkedHashSet<Long> deptIds) {
    return ApiLocaleResult.success(authPolicyDeptFacade.policyDeptAdd(policyId, deptIds));
  }

  @Operation(description = "Delete the associated departments of authorization policy.", operationId = "auth:policy:dept:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Deleted successfully")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/policy/{policyId}/dept")
  public void policyDeptDelete(
      @Parameter(name = "policyId", description = "Policy id", required = true) @PathVariable("policyId") Long policyId,
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @Parameter(name = "deptIds", description = "Department ids", required = true)
      @RequestParam("deptIds") HashSet<Long> deptIds) {
    authPolicyDeptFacade.policyDeptDelete(policyId, deptIds);
  }

  @Operation(description = "Query the departments list of authorization policy.", operationId = "auth:policy:dept:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/policy/{policyId}/dept")
  public ApiLocaleResult<PageResult<DeptListVo>> policyDeptList(
      @Parameter(name = "policyId", description = "Policy id", required = true) @PathVariable("policyId") Long policyId,
      @Valid @ParameterObject AuthPolicyDeptFindDto dto) {
    return ApiLocaleResult.success(authPolicyDeptFacade.policyDeptList(policyId, dto));
  }

  @Operation(description = "Query the unauthorized departments list of authorization policy.", operationId = "auth:policy:unauth:dept:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/policy/{policyId}/unauth/dept")
  public ApiLocaleResult<PageResult<DeptListVo>> policyUnauthDeptList(
      @Parameter(name = "policyId", description = "Policy id", required = true) @PathVariable("policyId") Long policyId,
      @Valid @ParameterObject AuthPolicyDeptFindDto dto) {
    return ApiLocaleResult.success(authPolicyDeptFacade.policyUnauthDeptList(policyId, dto));
  }

  @Operation(description = "Authorize the policies to department.", operationId = "auth:dept:policy:add")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created successfully")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/dept/{deptId}/policy")
  public ApiLocaleResult<List<IdKey<Long, Object>>> deptPolicyAdd(
      @Parameter(name = "deptId", description = "Department id", required = true) @PathVariable("deptId") Long deptId,
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @Parameter(name = "policyIds", description = "Policy ids", required = true)
      @RequestBody LinkedHashSet<Long> policyIds) {
    return ApiLocaleResult.success(authPolicyDeptFacade.deptPolicyAdd(deptId, policyIds));
  }

  @Operation(description = "Delete the authorization policies of department.", operationId = "auth:dept:policy:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Deleted successfully")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/dept/{deptId}/policy")
  public void deptPolicyDelete(
      @Parameter(name = "deptId", description = "Department id", required = true) @PathVariable("deptId") Long deptId,
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @Parameter(name = "policyIds", description = "Policy ids", required = true)
      @RequestParam("policyIds") HashSet<Long> policyIds) {
    authPolicyDeptFacade.deptPolicyDelete(deptId, policyIds);
  }

  @Operation(description = "Delete the authorization policies of departments.", operationId = "auth:dept:policy:delete:batch")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Deleted successfully")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/dept/policy")
  public void deptPolicyDeleteBatch(
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @Parameter(name = "deptIds", description = "Department ids", required = true)
      @RequestParam(value = "deptIds") HashSet<Long> deptIds,
      @Valid @Size(max = MAX_BATCH_SIZE) @Parameter(name = "policyIds", description = "Policy ids", required = false)
      @RequestParam(value = "policyIds", required = false) HashSet<Long> policyIds) {
    authPolicyDeptFacade.deptPolicyDeleteBatch(deptIds, policyIds);
  }

  @Operation(description = "Query the authorized policy list of department.", operationId = "auth:dept:policy:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/dept/{deptId}/policy")
  public ApiLocaleResult<PageResult<AuthPolicyVo>> deptPolicyList(
      @Parameter(name = "deptId", description = "Department id", required = true) @PathVariable("deptId") Long deptId,
      @Valid @ParameterObject AuthPolicyFindDto dto) {
    return ApiLocaleResult.success(authPolicyDeptFacade.deptPolicyList(deptId, dto));
  }

  @Operation(description = "Query the unauthorized policy list of department.", operationId = "auth:dept:unauth:policy:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/dept/{deptId}/unauth/policy")
  public ApiLocaleResult<PageResult<PolicyUnauthVo>> deptUnauthPolicyList(
      @Parameter(name = "deptId", description = "Department id", required = true) @PathVariable("deptId") Long deptId,
      @Valid @ParameterObject UnAuthPolicyFindDto dto) {
    return ApiLocaleResult.success(authPolicyDeptFacade.deptUnauthPolicyList(deptId, dto));
  }
}
