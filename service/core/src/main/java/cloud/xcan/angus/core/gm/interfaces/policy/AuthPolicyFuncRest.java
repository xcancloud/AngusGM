package cloud.xcan.angus.core.gm.interfaces.policy;

import static cloud.xcan.angus.api.commonlink.AASConstant.MAX_POLICY_FUNC_NUM;

import cloud.xcan.angus.core.gm.interfaces.policy.facade.AuthPolicyFuncFacade;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.func.AuthPolicyFuncTreeVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.func.AuthPolicyFuncVo;
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
import java.util.List;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "AuthPolicyFunc", description = "Provides a unified entry for querying the relationship between functions (button,  menus, panel) and authorization policies.")
@Validated
@RestController
@RequestMapping("/api/v1/auth/policy")
public class AuthPolicyFuncRest {

  @Resource
  private AuthPolicyFuncFacade authPolicyFuncFacade;

  @Operation(description = "Add application functions to authorization policy.", operationId = "auth:policy:func:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully")})
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/{id}/func")
  public ApiLocaleResult<?> add(
      @Parameter(name = "id", description = "Policy id", required = true) @PathVariable("id") Long policyId,
      @Valid @NotEmpty @Size(max = MAX_POLICY_FUNC_NUM) @RequestBody Set<Long> appFuncIds) {
    authPolicyFuncFacade.add(policyId, appFuncIds);
    return ApiLocaleResult.success();
  }

  @Operation(description = "Replace application functions of authorization policy.", operationId = "auth:policy:func:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully")})
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/{id}/func")
  public ApiLocaleResult<?> replace(
      @Parameter(name = "id", description = "Policy id", required = true) @PathVariable("id") Long policyId,
      @Valid @NotEmpty @Size(max = MAX_POLICY_FUNC_NUM) @RequestBody Set<Long> appFuncIds) {
    authPolicyFuncFacade.replace(policyId, appFuncIds);
    return ApiLocaleResult.success();
  }

  @Operation(description = "Delete application functions of authorization policy.", operationId = "auth:policy:func:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Replaced successfully")})
  @ResponseStatus(HttpStatus.OK)
  @DeleteMapping("/{id}/func")
  public ApiLocaleResult<?> delete(
      @Parameter(name = "id", description = "Policy id", required = true) @PathVariable("id") Long policyId,
      @Valid @NotEmpty @Size(max = MAX_POLICY_FUNC_NUM) @RequestBody Set<Long> appFuncIds) {
    authPolicyFuncFacade.delete(policyId, appFuncIds);
    return ApiLocaleResult.success();
  }

  @Operation(description = "Query the application functions list of authorization policy.", operationId = "auth:policy:func:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping("/{id}/func")
  public ApiLocaleResult<List<AuthPolicyFuncVo>> list(
      @Parameter(name = "id", description = "Policy id", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(authPolicyFuncFacade.list(id));
  }

  @Operation(description = "Query the application functions tree of authorization policy.", operationId = "auth:policy:func:tree")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping("/{id}/func/tree")
  public ApiLocaleResult<List<AuthPolicyFuncTreeVo>> tree(
      @Parameter(name = "id", description = "Policy id", required = true) @PathVariable("id") Long id) {
    return ApiLocaleResult.success(authPolicyFuncFacade.tree(id));
  }

}
