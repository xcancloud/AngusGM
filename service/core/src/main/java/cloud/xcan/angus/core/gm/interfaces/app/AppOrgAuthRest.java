package cloud.xcan.angus.core.gm.interfaces.app;

import cloud.xcan.angus.api.commonlink.AuthOrgType;
import cloud.xcan.angus.api.gm.app.vo.OrgAppAuthVo;
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


@Tag(name = "App Organization Authorization", description = "Manages user-specific application access permissions within organizations")
@Validated
@RestController
@RequestMapping("/api/v1")
public class AppOrgAuthRest {

  @Resource
  private AppOrgAuthFacade appOrgAuthFacade;

  @OperationClient
  @PreAuthorize("@PPS.isOpClient()")
  @Operation(summary = "Retrieve authorized tenants for application", operationId = "app:auth:tenant:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Authorized tenants retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Application not found")
  })
  @GetMapping("/app/{appId}/auth/tenant")
  public ApiLocaleResult<PageResult<AppAuthTenantVo>> appAuthTenant(
      @Parameter(name = "appId", description = "Application identifier", required = true) @PathVariable("appId") Long appId,
      @Valid @ParameterObject AuthAppOrgFindDto dto) {
    return ApiLocaleResult.success(appOrgAuthFacade.appAuthTenant(appId, dto));
  }

  @Operation(summary = "Retrieve authorized users for application", operationId = "app:auth:user:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Authorized users retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Application not found")
  })
  @GetMapping("/app/{appId}/auth/user")
  public ApiLocaleResult<PageResult<AppAuthUserVo>> appAuthUser(
      @Parameter(name = "appId", description = "Application identifier", required = true) @PathVariable("appId") Long appId,
      @Valid @ParameterObject AuthAppOrgUserSearchDto dto) {
    return ApiLocaleResult.success(appOrgAuthFacade.appAuthUser(appId, dto));
  }

  @Operation(summary = "Retrieve authorized departments for application", operationId = "app:auth:dept:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Authorized departments retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Application not found")
  })
  @GetMapping("/app/{appId}/auth/dept")
  public ApiLocaleResult<PageResult<AppAuthDeptVo>> appAuthDept(
      @Parameter(name = "appId", description = "Application identifier", required = true) @PathVariable("appId") Long appId,
      @Valid @ParameterObject AuthAppOrgSearchDto dto) {
    return ApiLocaleResult.success(appOrgAuthFacade.appAuthDept(appId, dto));
  }

  @Operation(summary = "Retrieve authorized groups for application", operationId = "app:auth:group:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Authorized groups retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Application not found")
  })
  @GetMapping("/app/{appId}/auth/group")
  public ApiLocaleResult<PageResult<AppAuthGroupVo>> appAuthGroup(
      @Parameter(name = "appId", description = "Application identifier", required = true) @PathVariable("appId") Long appId,
      @Valid @ParameterObject AuthAppOrgSearchDto dto) {
    return ApiLocaleResult.success(appOrgAuthFacade.appAuthGroup(appId, dto));
  }

  @Operation(summary = "Retrieve global authorized policies for application", operationId = "app:auth:global:all")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Global authorized policies retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Application not found")
  })
  @GetMapping("/app/{appId}/auth/global")
  public ApiLocaleResult<List<AppAuthPolicyVo>> appAuthGlobal(
      @ParameterObject @Parameter(name = "appId", description = "Application identifier", required = true) @PathVariable("appId") Long appId) {
    return ApiLocaleResult.success(appOrgAuthFacade.appAuthGlobal(appId));
  }

  @Operation(summary = "Check organization authorization for application", operationId = "app:auth:org:check")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Organization authorization check completed successfully"),
      @ApiResponse(responseCode = "404", description = "Application not found")
  })
  @GetMapping("/app/{appId}/auth/org/{orgType}/{orgId}/check")
  public ApiLocaleResult<Boolean> appAuthOrgCheck(
      @Parameter(name = "appId", description = "Application identifier", required = true) @PathVariable("appId") Long appId,
      @Parameter(name = "orgType", description = "Organization type", required = true) @PathVariable("orgType") AuthOrgType orgType,
      @Parameter(name = "orgId", description = "Organization identifier", required = true) @PathVariable("orgId") Long orgId) {
    return ApiLocaleResult.success(appOrgAuthFacade.appAuthOrgCheck(appId, orgType, orgId));
  }

  @Operation(summary = "Retrieve authorized application policies for organization", operationId = "app:auth:policy:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Authorized application policies retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Application not found")
  })
  @GetMapping("/app/{appId}/auth/org/{orgType}/{orgId}/policy")
  public ApiLocaleResult<PageResult<AppAuthPolicyVo>> appAuthPolicy(
      @Parameter(name = "appId", description = "Application identifier", required = true) @PathVariable("appId") Long appId,
      @Parameter(name = "orgType", description = "Organization type", required = true) @PathVariable("orgType") AuthOrgType orgType,
      @Parameter(name = "orgId", description = "Organization identifier", required = true) @PathVariable("orgId") Long orgId,
      @Valid AuthAppOrgSearchDto dto) {
    return ApiLocaleResult.success(appOrgAuthFacade.appAuthPolicy(appId, orgType, orgId, dto));
  }

  @Operation(summary = "Retrieve authorized applications for organization", operationId = "org:auth:app:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Authorized applications retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Organization not found")
  })
  @GetMapping("/org/{type}/{id}/auth/app")
  public ApiLocaleResult<List<OrgAppAuthVo>> orgAuthApp(
      @Parameter(name = "type", description = "Organization type", required = true) @PathVariable("type") AuthOrgType orgType,
      @Parameter(name = "id", description = "Organization identifier", required = true) @PathVariable("id") Long orgId,
      @Parameter(name = "joinPolicy", description = "Include policy information flag", required = false) @RequestParam(value = "joinPolicy", required = false, defaultValue = "false") Boolean joinPolicy) {
    return ApiLocaleResult.success(
        appOrgAuthFacade.orgAuthApp(orgType, orgId, joinPolicy != null ? joinPolicy : false));
  }

  @Operation(summary = "Check organization authorization for specific application", operationId = "org:auth:app:check")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Organization authorization check completed successfully"),
      @ApiResponse(responseCode = "404", description = "Organization not found")
  })
  @GetMapping("/org/{orgType}/{orgId}/auth/app/{appId}/check")
  public ApiLocaleResult<Boolean> orgAuthAppCheck(
      @Parameter(name = "orgType", description = "Organization type", required = true) @PathVariable("orgType") AuthOrgType orgType,
      @Parameter(name = "orgId", description = "Organization identifier", required = true) @PathVariable("orgId") Long orgId,
      @Parameter(name = "appId", description = "Application identifier", required = true) @PathVariable("appId") Long appId) {
    return ApiLocaleResult.success(appOrgAuthFacade.orgAuthAppCheck(orgType, orgId, appId));
  }

  @OperationClient
  @PreAuthorize("@PPS.isOpClient()")
  @Operation(summary = "Retrieve unauthorized tenants for application", operationId = "app:unauth:tenant:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Unauthorized tenants retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Application not found")
  })
  @GetMapping("/app/{appId}/unauth/tenant")
  public ApiLocaleResult<PageResult<AppUnauthTenantVo>> appUnauthTenant(
      @Parameter(name = "appId", description = "Application identifier", required = true) @PathVariable("appId") Long appId,
      @Valid @ParameterObject UnAuthFindDto dto) {
    return ApiLocaleResult.success(appOrgAuthFacade.appUnauthTenant(appId, dto));
  }

  @Operation(summary = "Retrieve unauthorized users for application", operationId = "app:unauth:user:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Unauthorized users retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Application not found")
  })
  @GetMapping("/app/{appId}/unauth/user")
  public ApiLocaleResult<PageResult<AppUnauthUserVo>> appUnauthUser(
      @Parameter(name = "appId", description = "Application identifier", required = true) @PathVariable("appId") Long appId,
      @Valid @ParameterObject UnAuthUserFindDto dto) {
    return ApiLocaleResult.success(appOrgAuthFacade.appUnauthUser(appId, dto));
  }

  @Operation(summary = "Retrieve unauthorized departments for application", operationId = "app:unauth:department:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Unauthorized departments retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Application not found")
  })
  @GetMapping("/app/{appId}/unauth/dept")
  public ApiLocaleResult<PageResult<AppUnauthDeptVo>> appUnauthDept(
      @Parameter(name = "appId", description = "Application identifier", required = true) @PathVariable("appId") Long appId,
      @Valid @ParameterObject UnAuthDeptFindDto dto) {
    return ApiLocaleResult.success(appOrgAuthFacade.appUnauthDept(appId, dto));
  }

  @Operation(summary = "Retrieve unauthorized groups for application", operationId = "app:unauth:group:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Unauthorized groups retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Application not found")
  })
  @GetMapping("/app/{appId}/unauth/group")
  public ApiLocaleResult<PageResult<AppUnauthGroupVo>> appUnauthGroup(
      @Parameter(name = "appId", description = "Application identifier", required = true) @PathVariable("appId") Long appId,
      @Valid @ParameterObject UnAuthGroupFindDto dto) {
    return ApiLocaleResult.success(appOrgAuthFacade.appUnauthGroup(appId, dto));
  }

  @Operation(summary = "Retrieve unauthorized policies for application", operationId = "app:unauth:policy:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Unauthorized policies retrieved successfully"),
      @ApiResponse(responseCode = "404", description = "Application not found")
  })
  @GetMapping("/app/{appId}/unauth/org/{orgType}/{orgId}/policy")
  public ApiLocaleResult<PageResult<AppAuthPolicyVo>> appUnauthPolicy(
      @Parameter(name = "appId", description = "Application identifier", required = true) @PathVariable("appId") Long appId,
      @Parameter(name = "orgType", description = "Organization type", required = true) @PathVariable("orgType") AuthOrgType orgType,
      @Parameter(name = "orgId", description = "Organization identifier", required = true) @PathVariable("orgId") Long orgId,
      @Valid @ParameterObject UnAuthFindDto dto) {
    return ApiLocaleResult.success(appOrgAuthFacade.appUnauthPolicy(appId, orgType, orgId, dto));
  }

  @Operation(summary = "Authorize application policies to users", operationId = "app:user:policy:auth")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Application policies authorized to users successfully"),
      @ApiResponse(responseCode = "404", description = "Application not found")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/app/{appId}/user/policy/auth")
  public ApiLocaleResult<List<IdKey<Long, Object>>> authUserPolicy(
      @Parameter(name = "appId", description = "Application identifier", required = true) @PathVariable("appId") Long appId,
      @Valid @RequestBody AuthOrgPolicyAuthDto dto) {
    return ApiLocaleResult.success(appOrgAuthFacade.authUserPolicy(appId, dto));
  }

  @Operation(summary = "Authorize application policies to departments", operationId = "app:dept:policy:auth")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Application policies authorized to departments successfully"),
      @ApiResponse(responseCode = "404", description = "Application not found")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/app/{appId}/dept/policy/auth")
  public ApiLocaleResult<List<IdKey<Long, Object>>> authDeptPolicy(
      @Parameter(name = "appId", description = "Application identifier", required = true) @PathVariable("appId") Long appId,
      @Valid @RequestBody AuthOrgPolicyAuthDto dto) {
    return ApiLocaleResult.success(appOrgAuthFacade.authDeptPolicy(appId, dto));
  }

  @Operation(summary = "Authorize application policies to groups", operationId = "app:group:policy:auth")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Application policies authorized to groups successfully"),
      @ApiResponse(responseCode = "404", description = "Application not found")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/app/{appId}/group/policy/auth")
  public ApiLocaleResult<List<IdKey<Long, Object>>> authGroupPolicy(
      @Parameter(name = "appId", description = "Application identifier", required = true) @PathVariable("appId") Long appId,
      @Valid @RequestBody AuthOrgPolicyAuthDto dto) {
    return ApiLocaleResult.success(appOrgAuthFacade.authGroupPolicy(appId, dto));
  }

  @Operation(summary = "Remove authorized users from application", operationId = "app:user:policy:auth:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Authorized users removed from application successfully"),
      @ApiResponse(responseCode = "404", description = "Application not found")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/app/{appId}/user/policy/auth")
  public void authUserDelete(
      @Parameter(name = "appId", description = "Application identifier", required = true) @PathVariable("appId") Long appId,
      @Valid @RequestBody AuthOrgPolicyAuthDeleteDto dto) {
    appOrgAuthFacade.authUserDelete(appId, dto);
  }

  @Operation(summary = "Remove authorized departments from application", operationId = "app:dept:policy:auth:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Authorized departments removed from application successfully"),
      @ApiResponse(responseCode = "404", description = "Application not found")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/app/{appId}/dept/policy/auth")
  public void authDeptDelete(
      @Parameter(name = "appId", description = "Application identifier", required = true) @PathVariable("appId") Long appId,
      @Valid @RequestBody AuthOrgPolicyAuthDeleteDto dto) {
    appOrgAuthFacade.authDeptDelete(appId, dto);
  }

  @Operation(summary = "Remove authorized groups from application", operationId = "app:group:policy:auth:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Authorized groups removed from application successfully"),
      @ApiResponse(responseCode = "404", description = "Application not found")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/app/{appId}/group/policy/auth")
  public void authGroupDelete(
      @Parameter(name = "appId", description = "Application identifier", required = true) @PathVariable("appId") Long appId,
      @Valid @RequestBody AuthOrgPolicyAuthDeleteDto dto) {
    appOrgAuthFacade.authGroupDelete(appId, dto);
  }

}
