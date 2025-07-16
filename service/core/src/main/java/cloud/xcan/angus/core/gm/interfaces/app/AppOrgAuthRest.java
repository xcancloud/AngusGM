package cloud.xcan.angus.core.gm.interfaces.app;

import cloud.xcan.angus.api.commonlink.AuthOrgType;
import cloud.xcan.angus.core.gm.interfaces.app.facade.AppOrgAuthFacade;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.org.AuthAppOrgFindDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.org.AuthAppOrgSearchDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.org.AuthAppOrgUserSearchDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.org.AuthOrgPolicyAuthDeleteDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.org.AuthOrgPolicyAuthDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.org.UnAuthDeptFindDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.org.UnAuthFindDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.org.UnAuthGroupFindDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.dto.org.UnAuthUserFindDto;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.org.AppAuthDeptVo;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.org.AppAuthGroupVo;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.org.AppAuthPolicyVo;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.org.AppAuthTenantVo;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.org.AppAuthUserVo;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.org.AppUnauthDeptVo;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.org.AppUnauthGroupVo;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.org.AppUnauthTenantVo;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.org.AppUnauthUserVo;
import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.org.OrgAppAuthVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.annotations.OperationClient;
import cloud.xcan.angus.spec.experimental.IdKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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


@Tag(name = "AppOrganizationAuthorization", description = "Retrieves user-specific application access permissions within an organization")
@Validated
@RestController
@RequestMapping("/api/v1")
public class AppOrgAuthRest {

  @Resource
  private AppOrgAuthFacade appOrgAuthFacade;

  @OperationClient
  @PreAuthorize("@PPS.isOpClient()")
  @Operation(summary = "Query the authorized tenant list of application", operationId = "app:auth:tenant:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "App not found")
  })
  @GetMapping("/app/{appId}/auth/tenant")
  public ApiLocaleResult<PageResult<AppAuthTenantVo>> appAuthTenant(
      @Parameter(name = "appId", description = "Application id", required = true) @PathVariable("appId") Long appId,
      @Valid @ParameterObject AuthAppOrgFindDto dto) {
    return ApiLocaleResult.success(appOrgAuthFacade.appAuthTenant(appId, dto));
  }

  @Operation(summary = "Query the authorized user list of application", operationId = "app:auth:user:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "App not found")
  })
  @GetMapping("/app/{appId}/auth/user")
  public ApiLocaleResult<PageResult<AppAuthUserVo>> appAuthUser(
      @Parameter(name = "appId", description = "Application id", required = true) @PathVariable("appId") Long appId,
      @Valid @ParameterObject AuthAppOrgUserSearchDto dto) {
    return ApiLocaleResult.success(appOrgAuthFacade.appAuthUser(appId, dto));
  }

  @Operation(summary = "Query the authorized departments list of application", operationId = "app:auth:dept:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "App not found")
  })
  @GetMapping("/app/{appId}/auth/dept")
  public ApiLocaleResult<PageResult<AppAuthDeptVo>> appAuthDept(
      @Parameter(name = "appId", description = "Application id", required = true) @PathVariable("appId") Long appId,
      @Valid @ParameterObject AuthAppOrgSearchDto dto) {
    return ApiLocaleResult.success(appOrgAuthFacade.appAuthDept(appId, dto));
  }

  @Operation(summary = "Query the authorized groups list of application", operationId = "app:auth:group:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "App not found")
  })
  @GetMapping("/app/{appId}/auth/group")
  public ApiLocaleResult<PageResult<AppAuthGroupVo>> appAuthGroup(
      @Parameter(name = "appId", description = "Application id", required = true) @PathVariable("appId") Long appId,
      @Valid @ParameterObject AuthAppOrgSearchDto dto) {
    return ApiLocaleResult.success(appOrgAuthFacade.appAuthGroup(appId, dto));
  }

  @Operation(summary = "Query the all global authorized policies of application", operationId = "app:auth:global:all")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "App not found")
  })
  @GetMapping("/app/{appId}/auth/global")
  public ApiLocaleResult<List<AppAuthPolicyVo>> appAuthGlobal(
      @ParameterObject @Parameter(name = "appId", description = "Application id", required = true) @PathVariable("appId") Long appId) {
    return ApiLocaleResult.success(appOrgAuthFacade.appAuthGlobal(appId));
  }

  @Operation(summary = "Check the authorized organization(Tenant/User/Department/Group) of application", operationId = "app:auth:org:check")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "App not found")
  })
  @GetMapping("/app/{appId}/auth/org/{orgType}/{orgId}/check")
  public ApiLocaleResult<Boolean> appAuthOrgCheck(
      @Parameter(name = "appId", description = "Application id", required = true) @PathVariable("appId") Long appId,
      @Parameter(name = "orgType", description = "Organization type", required = true) @PathVariable("orgType") AuthOrgType orgType,
      @Parameter(name = "orgId", description = "Organization id", required = true) @PathVariable("orgId") Long orgId) {
    return ApiLocaleResult.success(appOrgAuthFacade.appAuthOrgCheck(appId, orgType, orgId));
  }

  @Operation(summary = "Query the authorized application policies list of organization(Tenant/User/Department/Group)", operationId = "app:auth:policy:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "App not found")
  })
  @GetMapping("/app/{appId}/auth/org/{orgType}/{orgId}/policy")
  public ApiLocaleResult<PageResult<AppAuthPolicyVo>> appAuthPolicy(
      @Parameter(name = "appId", description = "Application id", required = true) @PathVariable("appId") Long appId,
      @Parameter(name = "orgType", description = "Organization type", required = true) @PathVariable("orgType") AuthOrgType orgType,
      @Parameter(name = "orgId", description = "Organization id", required = true) @PathVariable("orgId") Long orgId,
      @Valid AuthAppOrgSearchDto dto) {
    return ApiLocaleResult.success(appOrgAuthFacade.appAuthPolicy(appId, orgType, orgId, dto));
  }

  @Operation(summary = "Query the authorized application list of organization(Tenant/User/Department/Group)", operationId = "org:auth:app:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Organization not found")
  })
  @GetMapping("/org/{type}/{id}/auth/app")
  public ApiLocaleResult<List<OrgAppAuthVo>> orgAuthApp(
      @Parameter(name = "type", description = "Organization type", required = true) @PathVariable("type") AuthOrgType orgType,
      @Parameter(name = "id", description = "Organization id", required = true) @PathVariable("id") Long orgId,
      @Parameter(name = "joinPolicy", description = "Join policy flag", required = true) @RequestParam(value = "joinPolicy", required = false) Boolean joinPolicy) {
    return ApiLocaleResult.success(appOrgAuthFacade.orgAuthApp(orgType, orgId, joinPolicy));
  }

  @Operation(summary = "Check organization(Tenant/User/Department/Group) authorized application", operationId = "org:auth:app:check")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Organization not found")
  })
  @GetMapping("/org/{orgType}/{orgId}/auth/app/{appId}/check")
  public ApiLocaleResult<Boolean> orgAuthAppCheck(
      @Parameter(name = "orgType", description = "Organization type", required = true) @PathVariable("orgType") AuthOrgType orgType,
      @Parameter(name = "orgId", description = "Organization id", required = true) @PathVariable("orgId") Long orgId,
      @Parameter(name = "appId", description = "Application id", required = true) @PathVariable("appId") Long appId) {
    return ApiLocaleResult.success(appOrgAuthFacade.orgAuthAppCheck(orgType, orgId, appId));
  }

  @OperationClient
  @PreAuthorize("@PPS.isOpClient()")
  @Operation(summary = "Query the application unauthorized tenants", operationId = "app:unauth:tenant:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "App not found")
  })
  @GetMapping("/app/{appId}/unauth/tenant")
  public ApiLocaleResult<PageResult<AppUnauthTenantVo>> appUnauthTenant(
      @Parameter(name = "appId", description = "Application id", required = true) @PathVariable("appId") Long appId,
      @Valid @ParameterObject UnAuthFindDto dto) {
    return ApiLocaleResult.success(appOrgAuthFacade.appUnauthTenant(appId, dto));
  }

  @Operation(summary = "Query the application unauthorized users", operationId = "app:unauth:user:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "App not found")
  })
  @GetMapping("/app/{appId}/unauth/user")
  public ApiLocaleResult<PageResult<AppUnauthUserVo>> appUnauthUser(
      @Parameter(name = "appId", description = "Application id", required = true) @PathVariable("appId") Long appId,
      @Valid @ParameterObject UnAuthUserFindDto dto) {
    return ApiLocaleResult.success(appOrgAuthFacade.appUnauthUser(appId, dto));
  }

  @Operation(summary = "Query the application unauthorized departments", operationId = "app:unauth:department:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "App not found")
  })
  @GetMapping("/app/{appId}/unauth/dept")
  public ApiLocaleResult<PageResult<AppUnauthDeptVo>> appUnauthDept(
      @Parameter(name = "appId", description = "Application id", required = true) @PathVariable("appId") Long appId,
      @Valid @ParameterObject UnAuthDeptFindDto dto) {
    return ApiLocaleResult.success(appOrgAuthFacade.appUnauthDept(appId, dto));
  }

  @Operation(summary = "Query the application unauthorized groups", operationId = "app:unauth:group:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "App not found")
  })
  @GetMapping("/app/{appId}/unauth/group")
  public ApiLocaleResult<PageResult<AppUnauthGroupVo>> appUnauthGroup(
      @Parameter(name = "appId", description = "Application id", required = true) @PathVariable("appId") Long appId,
      @Valid @ParameterObject UnAuthGroupFindDto dto) {
    return ApiLocaleResult.success(appOrgAuthFacade.appUnauthGroup(appId, dto));
  }

  @Operation(summary = "Query the application unauthorized policies", operationId = "app:unauth:policy:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "App not found")
  })
  @GetMapping("/app/{appId}/unauth/org/{orgType}/{orgId}/policy")
  public ApiLocaleResult<PageResult<AppAuthPolicyVo>> appUnauthPolicy(
      @Parameter(name = "appId", description = "Application id", required = true) @PathVariable("appId") Long appId,
      @Parameter(name = "orgType", description = "Organization type", required = true) @PathVariable("orgType") AuthOrgType orgType,
      @Parameter(name = "orgId", description = "Organization id", required = true) @PathVariable("orgId") Long orgId,
      @Valid @ParameterObject UnAuthFindDto dto) {
    return ApiLocaleResult.success(appOrgAuthFacade.appUnauthPolicy(appId, orgType, orgId, dto));
  }

  @Operation(summary = "Authorize the application policies to the users", operationId = "app:user:policy:auth")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Created successfully"),
      @ApiResponse(responseCode = "404", description = "App not found")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/app/{appId}/user/policy/auth")
  public ApiLocaleResult<List<IdKey<Long, Object>>> authUserPolicy(
      @Parameter(name = "appId", description = "Application id", required = true) @PathVariable("appId") Long appId,
      @Valid @RequestBody AuthOrgPolicyAuthDto dto) {
    return ApiLocaleResult.success(appOrgAuthFacade.authUserPolicy(appId, dto));
  }

  @Operation(summary = "Authorize the application policies to the departments", operationId = "app:dept:policy:auth")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Created successfully"),
      @ApiResponse(responseCode = "404", description = "App not found")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/app/{appId}/dept/policy/auth")
  public ApiLocaleResult<List<IdKey<Long, Object>>> authDeptPolicy(
      @Parameter(name = "appId", description = "Application id", required = true) @PathVariable("appId") Long appId,
      @Valid @RequestBody AuthOrgPolicyAuthDto dto) {
    return ApiLocaleResult.success(appOrgAuthFacade.authDeptPolicy(appId, dto));
  }

  @Operation(summary = "Authorize the application policies to the groups", operationId = "app:group:policy:auth")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Created successfully"),
      @ApiResponse(responseCode = "404", description = "App not found")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/app/{appId}/group/policy/auth")
  public ApiLocaleResult<List<IdKey<Long, Object>>> authGroupPolicy(
      @Parameter(name = "appId", description = "Application id", required = true) @PathVariable("appId") Long appId,
      @Valid @RequestBody AuthOrgPolicyAuthDto dto) {
    return ApiLocaleResult.success(appOrgAuthFacade.authGroupPolicy(appId, dto));
  }

  @Operation(summary = "Delete the authorized users of application", operationId = "app:user:policy:auth:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Deleted successfully"),
      @ApiResponse(responseCode = "404", description = "App not found")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/app/{appId}/user/policy/auth")
  public void authUserDelete(
      @Parameter(name = "appId", description = "Application id", required = true) @PathVariable("appId") Long appId,
      @Valid @RequestBody AuthOrgPolicyAuthDeleteDto dto) {
    appOrgAuthFacade.authUserDelete(appId, dto);
  }

  @Operation(summary = "Delete the authorized departments of application", operationId = "app:dept:policy:auth:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Deleted successfully"),
      @ApiResponse(responseCode = "404", description = "App not found")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/app/{appId}/dept/policy/auth")
  public void authDeptDelete(
      @Parameter(name = "appId", description = "Application id", required = true) @PathVariable("appId") Long appId,
      @Valid @RequestBody AuthOrgPolicyAuthDeleteDto dto) {
    appOrgAuthFacade.authDeptDelete(appId, dto);
  }

  @Operation(summary = "Delete the authorized groups of application", operationId = "app:group:policy:auth:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Deleted successfully"),
      @ApiResponse(responseCode = "404", description = "App not found")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/app/{appId}/group/policy/auth")
  public void authGroupDelete(
      @Parameter(name = "appId", description = "Application id", required = true) @PathVariable("appId") Long appId,
      @Valid @RequestBody AuthOrgPolicyAuthDeleteDto dto) {
    appOrgAuthFacade.authGroupDelete(appId, dto);
  }

}
