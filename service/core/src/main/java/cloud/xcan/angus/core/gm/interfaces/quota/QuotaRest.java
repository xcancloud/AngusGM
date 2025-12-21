package cloud.xcan.angus.core.gm.interfaces.quota;

import cloud.xcan.angus.core.gm.interfaces.quota.facade.QuotaFacade;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.dto.ApplyTemplateDto;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.dto.QuotaAlertFindDto;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.dto.QuotaAlertHandleDto;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.dto.QuotaAlertRulesUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.dto.QuotaTemplateCreateDto;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.dto.TenantQuotaFindDto;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.dto.TenantQuotaUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.vo.ApplyTemplateResultVo;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.vo.QuotaAlertHandleResultVo;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.vo.QuotaAlertRulesVo;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.vo.QuotaAlertVo;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.vo.QuotaOverviewVo;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.vo.QuotaTemplateVo;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.vo.TenantQuotaDetailVo;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.vo.TenantQuotaVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Resource Quota REST API
 */
@Tag(name = "Quotas", description = "资源配额 - 租户资源配额管理、使用情况监控、配额告警")
@Validated
@RestController
@RequestMapping("/api/v1/quotas")
public class QuotaRest {

  @Resource
  private QuotaFacade quotaFacade;

  @Operation(operationId = "createQuotaTemplate", summary = "创建配额模板", description = "创建新的配额模板")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "创建成功"),
      @ApiResponse(responseCode = "400", description = "参数错误")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/templates")
  public ApiLocaleResult<QuotaTemplateVo> createTemplate(@Valid @RequestBody QuotaTemplateCreateDto dto) {
    return ApiLocaleResult.success(quotaFacade.createTemplate(dto));
  }

  @Operation(operationId = "updateTenantQuota", summary = "更新租户配额", description = "更新指定租户的配额设置")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "400", description = "参数错误"),
      @ApiResponse(responseCode = "404", description = "租户不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/tenants/{tenantId}")
  public ApiLocaleResult<TenantQuotaDetailVo> updateTenantQuota(
      @Parameter(description = "租户ID") @PathVariable String tenantId,
      @Valid @RequestBody TenantQuotaUpdateDto dto) {
    return ApiLocaleResult.success(quotaFacade.updateTenantQuota(tenantId, dto));
  }

  @Operation(operationId = "updateQuotaAlertRules", summary = "更新配额告警规则", description = "更新配额告警规则配置")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "400", description = "参数错误")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/alert-rules")
  public ApiLocaleResult<QuotaAlertRulesVo> updateAlertRules(@Valid @RequestBody QuotaAlertRulesUpdateDto dto) {
    return ApiLocaleResult.success(quotaFacade.updateAlertRules(dto));
  }

  @Operation(operationId = "handleQuotaAlert", summary = "处理配额告警", description = "处理指定的配额告警记录")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "处理成功"),
      @ApiResponse(responseCode = "400", description = "参数错误"),
      @ApiResponse(responseCode = "404", description = "告警记录不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @PatchMapping("/alerts/{id}/handle")
  public ApiLocaleResult<QuotaAlertHandleResultVo> handleAlert(
      @Parameter(description = "告警ID") @PathVariable String id,
      @Valid @RequestBody QuotaAlertHandleDto dto) {
    return ApiLocaleResult.success(quotaFacade.handleAlert(id, dto));
  }

  @Operation(operationId = "applyQuotaTemplate", summary = "应用模板到租户", description = "将配额模板应用到指定租户")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "应用成功"),
      @ApiResponse(responseCode = "400", description = "参数错误"),
      @ApiResponse(responseCode = "404", description = "租户或模板不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/tenants/{tenantId}/apply-template")
  public ApiLocaleResult<ApplyTemplateResultVo> applyTemplate(
      @Parameter(description = "租户ID") @PathVariable String tenantId,
      @Valid @RequestBody ApplyTemplateDto dto) {
    return ApiLocaleResult.success(quotaFacade.applyTemplate(tenantId, dto));
  }

  @Operation(operationId = "getQuotaOverview", summary = "获取资源配额概览", description = "获取系统资源配额使用概览")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/overview")
  public ApiLocaleResult<QuotaOverviewVo> getOverview() {
    return ApiLocaleResult.success(quotaFacade.getOverview());
  }

  @Operation(operationId = "listTenantQuotas", summary = "获取租户配额列表", description = "分页获取租户配额列表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/tenants")
  public ApiLocaleResult<PageResult<TenantQuotaVo>> listTenantQuotas(@Valid TenantQuotaFindDto dto) {
    return ApiLocaleResult.success(quotaFacade.listTenantQuotas(dto));
  }

  @Operation(operationId = "getTenantQuotaDetail", summary = "获取租户配额详情", description = "获取指定租户的配额详情")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功"),
      @ApiResponse(responseCode = "404", description = "租户不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/tenants/{tenantId}")
  public ApiLocaleResult<TenantQuotaDetailVo> getTenantQuotaDetail(
      @Parameter(description = "租户ID") @PathVariable String tenantId) {
    return ApiLocaleResult.success(quotaFacade.getTenantQuotaDetail(tenantId));
  }

  @Operation(operationId = "listQuotaTemplates", summary = "获取配额模板列表", description = "获取所有配额模板")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/templates")
  public ApiLocaleResult<List<QuotaTemplateVo>> listTemplates() {
    return ApiLocaleResult.success(quotaFacade.listTemplates());
  }

  @Operation(operationId = "getQuotaAlertRules", summary = "获取配额告警规则", description = "获取配额告警规则配置")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/alert-rules")
  public ApiLocaleResult<QuotaAlertRulesVo> getAlertRules() {
    return ApiLocaleResult.success(quotaFacade.getAlertRules());
  }

  @Operation(operationId = "listQuotaAlerts", summary = "获取配额告警记录", description = "分页获取配额告警记录")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/alerts")
  public ApiLocaleResult<PageResult<QuotaAlertVo>> listAlerts(@Valid QuotaAlertFindDto dto) {
    return ApiLocaleResult.success(quotaFacade.listAlerts(dto));
  }
}
