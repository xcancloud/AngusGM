package cloud.xcan.angus.core.gm.interfaces.sms.facade;

import cloud.xcan.angus.common.result.PageResult;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.*;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.*;

import java.util.List;

public interface SmsFacade {
    
    // ==================== 统计与记录 ====================
    
    SmsStatsVo getStats();
    
    PageResult<SmsRecordVo> listRecords(SmsRecordFindDto dto);
    
    // ==================== 短信发送 ====================
    
    SmsSendVo send(SmsSendDto dto);
    
    SmsSendBatchVo sendBatch(SmsSendBatchDto dto);
    
    SmsTestVo test(SmsTestDto dto);
    
    // ==================== 短信模板管理 ====================
    
    PageResult<SmsTemplateVo> listTemplates(SmsTemplateFindDto dto);
    
    SmsTemplateVo createTemplate(SmsTemplateCreateDto dto);
    
    SmsTemplateVo updateTemplate(Long id, SmsTemplateUpdateDto dto);
    
    void deleteTemplate(Long id);
    
    SmsTemplateStatusVo updateTemplateStatus(Long id, SmsTemplateStatusDto dto);
    
    // ==================== 服务商配置 ====================
    
    List<SmsProviderVo> listProviders();
    
    SmsProviderVo createProvider(SmsProviderCreateDto dto);
}
