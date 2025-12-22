package cloud.xcan.angus.core.gm.interfaces.email.facade;

import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.*;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.*;

public interface EmailFacade {
    
    EmailTemplateVo createTemplate(EmailTemplateCreateDto dto);
    
    EmailTemplateVo updateTemplate(Long id, EmailTemplateUpdateDto dto);
    
    EmailTemplateStatusVo updateTemplateStatus(Long id, EmailTemplateStatusDto dto);
    
    void deleteTemplate(Long id);
    
    EmailSmtpVo updateSmtpConfig(EmailSmtpUpdateDto dto);
    
    EmailSendVo send(EmailSendDto dto);
    
    EmailSendBatchVo sendBatch(EmailSendBatchDto dto);
    
    EmailSendVo sendCustom(EmailSendCustomDto dto);
    
    EmailStatsVo getStats();
    
    PageResult<EmailRecordVo> listRecords(EmailRecordFindDto dto);
    
    PageResult<EmailTemplateVo> listTemplates(EmailTemplateFindDto dto);
    
    EmailSmtpVo getSmtpConfig();
    
    EmailSmtpTestVo testSmtpConnection(EmailSmtpTestDto dto);
    
    EmailTrackingVo getEmailTracking(Long id);
}
