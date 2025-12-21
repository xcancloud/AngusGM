package cloud.xcan.angus.core.gm.interfaces.quota.facade;

import cloud.xcan.angus.core.gm.interfaces.quota.facade.dto.*;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.vo.*;
import cloud.xcan.angus.remote.PageResult;

import java.util.List;

/**
 * 资源配额门面接口
 */
public interface QuotaFacade {
    
    // ==================== 概览 ====================
    
    /**
     * 获取资源配额概览
     */
    QuotaOverviewVo getOverview();
    
    // ==================== 租户配额管理 ====================
    
    /**
     * 获取租户配额列表
     */
    PageResult<TenantQuotaVo> listTenantQuotas(TenantQuotaFindDto dto);
    
    /**
     * 获取租户配额详情
     */
    TenantQuotaDetailVo getTenantQuotaDetail(String tenantId);
    
    /**
     * 更新租户配额
     */
    TenantQuotaDetailVo updateTenantQuota(String tenantId, TenantQuotaUpdateDto dto);
    
    // ==================== 配额模板管理 ====================
    
    /**
     * 获取配额模板列表
     */
    List<QuotaTemplateVo> listTemplates();
    
    /**
     * 创建配额模板
     */
    QuotaTemplateVo createTemplate(QuotaTemplateCreateDto dto);
    
    /**
     * 应用模板到租户
     */
    ApplyTemplateResultVo applyTemplate(String tenantId, ApplyTemplateDto dto);
    
    // ==================== 配额告警 ====================
    
    /**
     * 获取配额告警规则
     */
    QuotaAlertRulesVo getAlertRules();
    
    /**
     * 更新配额告警规则
     */
    QuotaAlertRulesVo updateAlertRules(QuotaAlertRulesUpdateDto dto);
    
    /**
     * 获取配额告警记录
     */
    PageResult<QuotaAlertVo> listAlerts(QuotaAlertFindDto dto);
    
    /**
     * 处理配额告警
     */
    QuotaAlertHandleResultVo handleAlert(String id, QuotaAlertHandleDto dto);
}
