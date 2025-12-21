package cloud.xcan.angus.core.gm.interfaces.email.facade;

import cloud.xcan.angus.common.result.PageResult;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.*;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.*;

public interface EmailFacade {
    
    // ==================== 邮件模板管理 ====================
    
    EmailTemplateVo createTemplate(EmailTemplateCreateDto dto);
    
    EmailTemplateVo updateTemplate(Long id, EmailTemplateUpdateDto dto);
    
    EmailTemplateStatusVo updateTemplateStatus(Long id, EmailTemplateStatusDto dto);
    
    void deleteTemplate(Long id);
    
    // ==================== SMTP配置 ====================
    
    EmailSmtpVo updateSmtpConfig(EmailSmtpUpdateDto dto);
    
    // ==================== 邮件发送 ====================
    
    EmailSendVo send(EmailSendDto dto);
    
    EmailSendBatchVo sendBatch(EmailSendBatchDto dto);
    
    EmailSendVo sendCustom(EmailSendCustomDto dto);
    
    // ==================== 查询 ====================
    
    EmailStatsVo getStats();
    
    PageResult<EmailRecordVo> listRecords(EmailRecordFindDto dto);
    
    PageResult<EmailTemplateVo> listTemplates(EmailTemplateFindDto dto);
    
    EmailSmtpVo getSmtpConfig();
    
    EmailSmtpTestVo testSmtpConnection(EmailSmtpTestDto dto);
}
