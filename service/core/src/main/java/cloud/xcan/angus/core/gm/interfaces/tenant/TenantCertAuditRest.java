package cloud.xcan.angus.core.gm.interfaces.tenant;


import static cloud.xcan.angus.api.commonlink.UCConstant.TOP_TENANT_ADMIN;

import cloud.xcan.angus.api.gm.tenant.dto.audit.TenantRealNameAuditDto;
import cloud.xcan.angus.api.gm.tenant.dto.audit.TenantRealNameSubmitDto;
import cloud.xcan.angus.api.gm.tenant.vo.audit.TenantAuditDetailVo;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.TenantCertAuditFacade;
import cloud.xcan.angus.core.spring.condition.CloudServiceEditionCondition;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.spec.annotations.OperationClient;
import cloud.xcan.angus.spec.annotations.TenantClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Conditional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "TenantCertAudit", description = "Verifies and audits tenant identity documents to ensure compliance and authenticity.")
@Conditional(CloudServiceEditionCondition.class)
@OperationClient
@Validated
@RestController
@RequestMapping("/api/v1/tenant/cert")
public class TenantCertAuditRest {

  @Resource
  private TenantCertAuditFacade tenantCertAuditFacade;

  @TenantClient
  @Operation(summary = "Submit real-name authentication certificate.", operationId = "tenant:certificate:submit")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Submit successfully")})
  @PostMapping("/audit/submit")
  public ApiLocaleResult<?> authSubmit(@Valid @RequestBody TenantRealNameSubmitDto dto) {
    tenantCertAuditFacade.authSubmit(dto);
    return ApiLocaleResult.success();
  }

  @PreAuthorize("@PPS.hasToPolicy('" + TOP_TENANT_ADMIN + "')")
  @OperationClient
  @Operation(summary = "Audit tenant certificate information.", operationId = "tenant:certificate:audit")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Audit succeeded")})
  @PatchMapping("/audit")
  public ApiLocaleResult<?> audit(@Valid @RequestBody TenantRealNameAuditDto dto) {
    tenantCertAuditFacade.audit(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Check the real-name audit of tenant.", operationId = "tenant:certificate:audit:check")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully audit")})
  @GetMapping("/audit/check")
  public ApiLocaleResult<?> check() {
    tenantCertAuditFacade.check();
    return ApiLocaleResult.success();
  }

  @Operation(summary = "Quote the detail of tenant real-name audit.", operationId = "tenant:certificate:audit:detail")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully audit")})
  @GetMapping("/audit")
  public ApiLocaleResult<TenantAuditDetailVo> auditDetail() {
    return ApiLocaleResult.success(tenantCertAuditFacade.detail());
  }

}
