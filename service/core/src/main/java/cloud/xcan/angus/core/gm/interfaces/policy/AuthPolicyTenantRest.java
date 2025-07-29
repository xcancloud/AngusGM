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


@Tag(name = "Auth Policy Tenant", description = "REST API endpoints for managing authorization policy and tenant relationships")
@Validated
@RestController
@RequestMapping("/api/v1/auth/tenant")
public class AuthPolicyTenantRest {

  @Resource
  private AuthPolicyTenantFacade authPolicyTenantFacade;

  @Operation(summary = "Set default authorization policy for user access to application", operationId = "auth:tenant:app:policy:default:replace")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Default policy set successfully"),
      @ApiResponse(responseCode = "404", description = "Application or policy not found")
  })
  @PutMapping("/app/{appId}/policy/default/{policyId}")
  public ApiLocaleResult<?> defaultPolicySet(
      @Parameter(name = "appId", description = "Application identifier", required = true) @PathVariable("appId") Long appId,
      @Parameter(name = "policyId", description = "Authorization policy identifier", required = true) @PathVariable("policyId") Long policyId) {
    authPolicyTenantFacade.defaultPolicySet(appId, policyId);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Remove default authorization policy for user access to application", operationId = "auth:tenant:app:policy:default:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Default policy removed successfully")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/app/{appId}/policy/default")
  public void defaultPolicyDelete(
      @Parameter(name = "appId", description = "Application identifier", required = true) @PathVariable("appId") Long appId) {
    authPolicyTenantFacade.defaultPolicyDelete(appId);
  }

  @Operation(summary = "Retrieve default authorization policies for all authorized applications", operationId = "auth:tenant:app:policy:default:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Default policies retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "No applications found")
  })
  @GetMapping(value = "/app/default")
  public ApiLocaleResult<List<AuthAppDefaultPolicyVo>> defaultPolicy() {
    return ApiLocaleResult.success(authPolicyTenantFacade.defaultPolicy());
  }

  @Operation(summary = "Retrieve all applications authorized for the tenant", operationId = "auth:tenant:app:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Authorized applications retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "No applications found")
  })
  @GetMapping(value = "/app")
  public ApiLocaleResult<List<AppVo>> tenantAppList() {
    return ApiLocaleResult.success(authPolicyTenantFacade.tenantAppList());
  }

  @Operation(summary = "Retrieve all functions for an authorized application", operationId = "auth:tenant:app:func:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Application functions retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Application not found")
  })
  @GetMapping(value = "/app/{appId}/func")
  public ApiLocaleResult<List<AuthPolicyFuncVo>> tenantAppFuncList(
      @Parameter(name = "appId", description = "Application identifier", required = true) @PathVariable("appId") Long appId) {
    return ApiLocaleResult.success(authPolicyTenantFacade.tenantAppFuncList(appId));
  }

  @Operation(summary = "Retrieve function tree for an authorized application", operationId = "auth:tenant:app:func:tree")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Application function tree retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Application not found")})
  @GetMapping(value = "/app/{appId}/func/tree")
  public ApiLocaleResult<List<AuthPolicyFuncTreeVo>> tenantAppFuncTree(
      @Valid @PathVariable("appId") @Parameter(name = "appId", description = "Application identifier", required = true) Long appId) {
    return ApiLocaleResult.success(authPolicyTenantFacade.tenantAppFuncTree(appId));
  }

}
