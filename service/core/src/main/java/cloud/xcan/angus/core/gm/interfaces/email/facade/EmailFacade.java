package cloud.xcan.angus.core.gm.interfaces.email.facade;

import cloud.xcan.angus.common.result.PageResult;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.*;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.*;

public interface EmailFacade {
    
    // ==================== 统计与记录 ====================
    
    EmailStatsVo getStats();
    
    PageResult<EmailRecordVo> listRecords(EmailRecordFindDto dto);
    
    EmailTrackingVo getEmailStats(Long id);
    
    // ==================== 邮件发送 ====================
    
    EmailSendVo send(EmailSendDto dto);
    
    EmailSendBatchVo sendBatch(EmailSendBatchDto dto);
    
    EmailSendVo sendCustom(EmailSendCustomDto dto);
    
    // ==================== 邮件模板管理 ====================
    
    PageResult<EmailTemplateVo> listTemplates(EmailTemplateFindDto dto);
    
    EmailTemplateVo createTemplate(EmailTemplateCreateDto dto);
    
    EmailTemplateVo updateTemplate(Long id, EmailTemplateUpdateDto dto);
    
    void deleteTemplate(Long id);
    
    EmailTemplateStatusVo updateTemplateStatus(Long id, EmailTemplateStatusDto dto);
    
    // ==================== SMTP配置 ====================
    
    EmailSmtpVo getSmtpConfig();
    
    EmailSmtpVo updateSmtpConfig(EmailSmtpUpdateDto dto);
    
    EmailSmtpTestVo testSmtpConnection(EmailSmtpTestDto dto);
}
