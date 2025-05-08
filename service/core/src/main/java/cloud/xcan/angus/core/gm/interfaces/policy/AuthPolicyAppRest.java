package cloud.xcan.angus.core.gm.interfaces.policy;

import cloud.xcan.angus.core.gm.interfaces.app.facade.vo.AppDetailVo;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.AuthPolicyAppFacade;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.dto.app.AppPolicyFindDto;
import cloud.xcan.angus.core.gm.interfaces.policy.facade.vo.app.AppPolicyVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "AuthPolicyApp", description = "Provides a unified entry for querying the relationship between applications and authorization policies.")
@Validated
@RestController
@RequestMapping("/api/v1")
public class AuthPolicyAppRest {

  @Resource
  private AuthPolicyAppFacade authPolicyAppFacade;

  @Operation(description = "Query the authorization policy list of application.", operationId = "app:policy:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/app/{appId}/policy")
  public ApiLocaleResult<PageResult<AppPolicyVo>> appPolicyList(
      @Parameter(name = "appId", description = "Application id", required = true) @PathVariable("appId") Long appId,
      @Valid @ParameterObject AppPolicyFindDto dto) {
    return ApiLocaleResult.success(authPolicyAppFacade.appPolicyList(appId, dto));
  }

  @Operation(description = "Query the application detail of authorization policy.", operationId = "policy:app:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Retrieved successfully")})
  @GetMapping("/policy/{policyId}/app")
  public ApiLocaleResult<AppDetailVo> policyAppDetail(
      @Parameter(name = "policyId", description = "Policy id", required = true) @PathVariable("policyId") Long policyId) {
    return ApiLocaleResult.success(authPolicyAppFacade.policyAppDetail(policyId));
  }

}
