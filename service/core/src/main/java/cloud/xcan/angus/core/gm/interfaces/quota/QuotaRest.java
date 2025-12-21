package cloud.xcan.angus.core.gm.interfaces.quota;

import cloud.xcan.angus.core.gm.interfaces.quota.facade.QuotaFacade;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.dto.*;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.vo.*;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 资源配额REST接口
 * 
 * 租户资源配额管理、使用情况监控、配额告警
 */
@Tag(name = "Quotas", description = "资源配额 - 租户资源配额管理、使用情况监控、配额告警")
@RestController
@RequestMapping("/api/v1/quotas")
@RequiredArgsConstructor
public class QuotaRest {
    
    private final QuotaFacade quotaFacade;
    
    // ==================== 概览 ====================
    
    @Operation(summary = "获取资源配额概览", description = "获取系统资源配额使用概览")
    @GetMapping("/overview")
    public ApiLocaleResult<QuotaOverviewVo> getOverview() {
        return ApiLocaleResult.success(quotaFacade.getOverview());
    }
    
    // ==================== 租户配额管理 ====================
    
    @Operation(summary = "获取租户配额列表", description = "分页获取租户配额列表")
    @GetMapping("/tenants")
    public ApiLocaleResult<PageResult<TenantQuotaVo>> listTenantQuotas(@Valid TenantQuotaFindDto dto) {
        return ApiLocaleResult.success(quotaFacade.listTenantQuotas(dto));
    }
    
    @Operation(summary = "获取租户配额详情", description = "获取指定租户的配额详情")
    @GetMapping("/tenants/{tenantId}")
    public ApiLocaleResult<TenantQuotaDetailVo> getTenantQuotaDetail(
            @Parameter(description = "租户ID") @PathVariable String tenantId) {
        return ApiLocaleResult.success(quotaFacade.getTenantQuotaDetail(tenantId));
    }
    
    @Operation(summary = "更新租户配额", description = "更新指定租户的配额设置")
    @PutMapping("/tenants/{tenantId}")
    public ApiLocaleResult<TenantQuotaDetailVo> updateTenantQuota(
            @Parameter(description = "租户ID") @PathVariable String tenantId,
            @Valid @RequestBody TenantQuotaUpdateDto dto) {
        return ApiLocaleResult.success(quotaFacade.updateTenantQuota(tenantId, dto));
    }
    
    // ==================== 配额模板管理 ====================
    
    @Operation(summary = "获取配额模板列表", description = "获取所有配额模板")
    @GetMapping("/templates")
    public ApiLocaleResult<List<QuotaTemplateVo>> listTemplates() {
        return ApiLocaleResult.success(quotaFacade.listTemplates());
    }
    
    @Operation(summary = "创建配额模板", description = "创建新的配额模板")
    @PostMapping("/templates")
    public ApiLocaleResult<QuotaTemplateVo> createTemplate(@Valid @RequestBody QuotaTemplateCreateDto dto) {
        return ApiLocaleResult.success(quotaFacade.createTemplate(dto));
    }
    
    @Operation(summary = "应用模板到租户", description = "将配额模板应用到指定租户")
    @PostMapping("/tenants/{tenantId}/apply-template")
    public ApiLocaleResult<ApplyTemplateResultVo> applyTemplate(
            @Parameter(description = "租户ID") @PathVariable String tenantId,
            @Valid @RequestBody ApplyTemplateDto dto) {
        return ApiLocaleResult.success(quotaFacade.applyTemplate(tenantId, dto));
    }
    
    // ==================== 配额告警 ====================
    
    @Operation(summary = "获取配额告警规则", description = "获取配额告警规则配置")
    @GetMapping("/alert-rules")
    public ApiLocaleResult<QuotaAlertRulesVo> getAlertRules() {
        return ApiLocaleResult.success(quotaFacade.getAlertRules());
    }
    
    @Operation(summary = "更新配额告警规则", description = "更新配额告警规则配置")
    @PutMapping("/alert-rules")
    public ApiLocaleResult<QuotaAlertRulesVo> updateAlertRules(@Valid @RequestBody QuotaAlertRulesUpdateDto dto) {
        return ApiLocaleResult.success(quotaFacade.updateAlertRules(dto));
    }
    
    @Operation(summary = "获取配额告警记录", description = "分页获取配额告警记录")
    @GetMapping("/alerts")
    public ApiLocaleResult<PageResult<QuotaAlertVo>> listAlerts(@Valid QuotaAlertFindDto dto) {
        return ApiLocaleResult.success(quotaFacade.listAlerts(dto));
    }
    
    @Operation(summary = "处理配额告警", description = "处理指定的配额告警记录")
    @PatchMapping("/alerts/{id}/handle")
    public ApiLocaleResult<QuotaAlertHandleResultVo> handleAlert(
            @Parameter(description = "告警ID") @PathVariable String id,
            @Valid @RequestBody QuotaAlertHandleDto dto) {
        return ApiLocaleResult.success(quotaFacade.handleAlert(id, dto));
    }
}
