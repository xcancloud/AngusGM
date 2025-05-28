package cloud.xcan.angus.core.gm.interfaces.policy;

import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.AppVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.AuthPolicyTenantFacade;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.func.AuthPolicyFuncTreeVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.func.AuthPolicyFuncVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.org.AuthAppDefaultPolicyVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "AuthPolicyTenant", description = "Provides a unified entry for querying the relationship between tenant, application and authorization policies.")
@Validated
@RestController
@RequestMapping("/api/v1/auth/tenant")
public class AuthPolicyTenantRest {

  @Resource
  private AuthPolicyTenantFacade authPolicyTenantFacade;

  @Operation(summary = "Set the default policy of user access application.", operationId = "auth:tenant:app:policy:default:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Set successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")
  })
  @PutMapping("/app/{appId}/policy/default/{policyId}")
  public ApiLocaleResult<?> defaultPolicySet(
      @Parameter(name = "appId", description = "Application id", required = true) @PathVariable("appId") Long appId,
      @Parameter(name = "policyId", description = "Authorization policy id", required = true) @PathVariable("policyId") Long policyId) {
    authPolicyTenantFacade.defaultPolicySet(appId, policyId);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Delete user access application default policy, prevent new users to access the application.", operationId = "auth:tenant:app:policy:default:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Deleted successfully")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/app/{appId}/policy/default")
  public void defaultPolicyDelete(
      @Parameter(name = "appId", description = "Application id", required = true) @PathVariable("appId") Long appId) {
    authPolicyTenantFacade.defaultPolicyDelete(appId);
  }

  @Operation(summary = "Query the default policies of all authorized applications.", operationId = "auth:tenant:app:policy:default:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping(value = "/app/default")
  public ApiLocaleResult<List<AuthAppDefaultPolicyVo>> defaultPolicy() {
    return ApiLocaleResult.success(authPolicyTenantFacade.defaultPolicy());
  }

  @Operation(summary = "Query all authorized applications of tenant.", operationId = "auth:tenant:app:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping(value = "/app")
  public ApiLocaleResult<List<AppVo>> tenantAppList() {
    return ApiLocaleResult.success(authPolicyTenantFacade.tenantAppList());
  }

  @Operation(summary = "Query all functions list of authorized application.", operationId = "auth:tenant:app:func:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping(value = "/app/{appId}/func")
  public ApiLocaleResult<List<AuthPolicyFuncVo>> tenantAppFuncList(
      @Parameter(name = "appId", description = "Application id", required = true) @PathVariable("appId") Long appId) {
    return ApiLocaleResult.success(authPolicyTenantFacade.tenantAppFuncList(appId));
  }

  @Operation(summary = "Query all functions tree of authorized application.", operationId = "auth:tenant:app:func:tree")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Resource not found")})
  @GetMapping(value = "/app/{appId}/func/tree")
  public ApiLocaleResult<List<AuthPolicyFuncTreeVo>> tenantAppFuncTree(
      @Valid @PathVariable("appId") @Parameter(name = "appId", description = "Application id", required = true) Long appId) {
    return ApiLocaleResult.success(authPolicyTenantFacade.tenantAppFuncTree(appId));
  }

}
