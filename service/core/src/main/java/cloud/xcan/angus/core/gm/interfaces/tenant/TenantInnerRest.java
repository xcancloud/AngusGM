package cloud.xcan.angus.core.gm.interfaces.tenant;


import cloud.xcan.angus.api.gm.tenant.dto.TenantFindDto;
import cloud.xcan.angus.api.gm.tenant.vo.TenantVo;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.TenantFacade;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@PreAuthorize("hasAuthority('SCOPE_inner_api_trust')")
@Tag(name = "Tenant - Innernal", description = "Internal system tenant management gateway. Provides secure internal API access for tenant operations and administrative functions")
@Validated
@RestController
@RequestMapping("/innerapi/v1/tenant")
public class TenantInnerRest {

  @Resource
  private TenantFacade tenantFacade;

  @Operation(summary = "Get paginated list of tenant accounts", operationId = "tenant:list:inner")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Tenant list retrieved successfully")})
  @GetMapping
  public ApiLocaleResult<PageResult<TenantVo>> list(@Valid TenantFindDto dto) {
    return ApiLocaleResult.success(tenantFacade.list(dto));
  }

}
