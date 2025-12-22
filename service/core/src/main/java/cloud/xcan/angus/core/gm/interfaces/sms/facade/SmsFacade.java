package cloud.xcan.angus.core.gm.interfaces.sms.facade;

import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.*;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.*;
import cloud.xcan.angus.remote.PageResult;

import java.util.List;

/**
 * <p>SMS facade interface</p>
 */
public interface SmsFacade {
    
    // ==================== 短信模板管理 ====================
    
    SmsTemplateVo createTemplate(SmsTemplateCreateDto dto);
    
    SmsTemplateVo updateTemplate(Long id, SmsTemplateUpdateDto dto);
    
    SmsTemplateStatusVo updateTemplateStatus(Long id, SmsTemplateStatusDto dto);
    
    void deleteTemplate(Long id);
    
    // ==================== 服务商配置 ====================
    
    SmsProviderVo createProvider(SmsProviderCreateDto dto);
    
    // ==================== 短信发送 ====================
    
    SmsSendVo send(SmsSendDto dto);
    
    SmsSendBatchVo sendBatch(SmsSendBatchDto dto);
    
    SmsTestVo test(SmsTestDto dto);
    
    // ==================== 查询 ====================
    
    SmsStatsVo getStats();
    
    PageResult<SmsRecordVo> listRecords(SmsRecordFindDto dto);
    
    PageResult<SmsTemplateVo> listTemplates(SmsTemplateFindDto dto);
    
    List<SmsProviderVo> listProviders();
}
