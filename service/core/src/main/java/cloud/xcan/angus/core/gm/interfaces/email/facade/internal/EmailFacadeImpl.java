package cloud.xcan.angus.core.gm.interfaces.email.facade.internal;

import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.core.gm.application.cmd.email.EmailCmd;
import cloud.xcan.angus.core.gm.application.cmd.email.EmailSmtpCmd;
import cloud.xcan.angus.core.gm.application.cmd.email.EmailTemplateCmd;
import cloud.xcan.angus.core.gm.application.query.email.EmailQuery;
import cloud.xcan.angus.core.gm.application.query.email.EmailSmtpQuery;
import cloud.xcan.angus.core.gm.application.query.email.EmailTemplateQuery;
import cloud.xcan.angus.core.gm.domain.email.Email;
import cloud.xcan.angus.core.gm.domain.email.EmailSmtp;
import cloud.xcan.angus.core.gm.domain.email.EmailTemplate;
import cloud.xcan.angus.core.gm.interfaces.email.facade.EmailFacade;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailRecordFindDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailSendBatchDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailSendCustomDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailSendDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailSmtpTestDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailSmtpUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailTemplateCreateDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailTemplateFindDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailTemplateStatusDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailTemplateUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.internal.assembler.EmailAssembler;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailRecordVo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailSendBatchVo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailSendVo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailSmtpTestVo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailSmtpVo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailStatsVo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailTemplateStatusVo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailTemplateVo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailTrackingVo;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * Email facade implementation
 */
@Service
public class EmailFacadeImpl implements EmailFacade {
    
    @Resource
    private EmailCmd emailCmd;
    
    @Resource
    private EmailQuery emailQuery;
    
    @Resource
    private EmailTemplateCmd emailTemplateCmd;
    
    @Resource
    private EmailTemplateQuery emailTemplateQuery;
    
    @Resource
    private EmailSmtpCmd emailSmtpCmd;
    
    @Resource
    private EmailSmtpQuery emailSmtpQuery;
    
    // ==================== 邮件模板管理 ====================
    
    @Override
    public EmailTemplateVo createTemplate(EmailTemplateCreateDto dto) {
        EmailTemplate template = EmailAssembler.toCreateTemplateDomain(dto);
        EmailTemplate saved = emailTemplateCmd.create(template);
        return EmailAssembler.toTemplateVo(saved);
    }
    
    @Override
    public EmailTemplateVo updateTemplate(Long id, EmailTemplateUpdateDto dto) {
        EmailTemplate template = EmailAssembler.toUpdateTemplateDomain(id, dto);
        EmailTemplate saved = emailTemplateCmd.update(template);
        return EmailAssembler.toTemplateVo(saved);
    }
    
    @Override
    public EmailTemplateStatusVo updateTemplateStatus(Long id, EmailTemplateStatusDto dto) {
        EmailTemplate template = emailTemplateCmd.updateStatus(id, dto.getStatus());
        EmailTemplateStatusVo vo = new EmailTemplateStatusVo();
        vo.setId(id);
        vo.setStatus(dto.getStatus());
        // Get modifiedDate using reflection since EmailTemplate uses @Getter
        try {
            java.lang.reflect.Method getModifiedDateMethod = template.getClass().getMethod("getModifiedDate");
            Object modifiedDate = getModifiedDateMethod.invoke(template);
            vo.setModifiedDate(modifiedDate != null ? (java.time.LocalDateTime) modifiedDate : null);
        } catch (Exception e) {
            vo.setModifiedDate(null);
        }
        return vo;
    }
    
    @Override
    public void deleteTemplate(Long id) {
        emailTemplateCmd.delete(id);
    }

    // ==================== SMTP配置 ====================
    
    @Override
    public EmailSmtpVo updateSmtpConfig(EmailSmtpUpdateDto dto) {
        EmailSmtp smtp = EmailAssembler.toUpdateSmtpDomain(dto);
        // Try to find existing default config
        EmailSmtp existing = emailSmtpQuery.findDefault().orElse(null);
        if (existing != null) {
            smtp.setId(existing.getId());
        }
        EmailSmtp saved = emailSmtpCmd.save(smtp);
        return EmailAssembler.toSmtpVo(saved);
    }

    // ==================== 邮件发送 ====================
    
    @Override
    public EmailSendVo send(EmailSendDto dto) {
        // Find template
        EmailTemplate template = emailTemplateQuery.findAndCheck(dto.getTemplateId());
        
        // Convert to Email domain
        Email email = EmailAssembler.toSendEmailDomain(dto, template);
        
        // Create email record
        Email created = emailCmd.create(email);
        
        // Send email (async in real implementation)
        // Get id using reflection since Email extends BaseEntity which may not expose getId() directly
        Long emailId = null;
        try {
            java.lang.reflect.Method getIdMethod = created.getClass().getMethod("getId");
            Object id = getIdMethod.invoke(created);
            emailId = id != null ? (Long) id : null;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get email id", e);
        }
        emailCmd.send(emailId);
        
        return EmailAssembler.toSendVo(created);
    }
    
    @Override
    public EmailSendBatchVo sendBatch(EmailSendBatchDto dto) {
        // Find template
        EmailTemplate template = emailTemplateQuery.findAndCheck(dto.getTemplateId());
        
        EmailSendBatchVo vo = new EmailSendBatchVo();
        vo.setTotalCount(dto.getTo().size());
        vo.setSuccessCount(0);
        vo.setFailedCount(0);
        List<EmailSendBatchVo.EmailSendResultVo> results = new ArrayList<>();
        
        for (String to : dto.getTo()) {
            try {
                // Create email for each recipient
                EmailSendDto singleDto = new EmailSendDto();
                singleDto.setTo(to);
                singleDto.setTemplateId(dto.getTemplateId());
                singleDto.setParams(dto.getParams());
                
                Email email = EmailAssembler.toSendEmailDomain(singleDto, template);
                Email created = emailCmd.create(email);
                // Get id using reflection since Email extends BaseEntity which may not expose getId() directly
                Long emailId = null;
                try {
                    java.lang.reflect.Method getIdMethod = created.getClass().getMethod("getId");
                    Object id = getIdMethod.invoke(created);
                    emailId = id != null ? (Long) id : null;
                } catch (Exception e) {
                    throw new RuntimeException("Failed to get email id", e);
                }
                emailCmd.send(emailId);
                
                EmailSendBatchVo.EmailSendResultVo result = new EmailSendBatchVo.EmailSendResultVo();
                result.setTo(to);
                result.setStatus("成功");
                result.setMessageId(created.getExternalId());
                results.add(result);
                vo.setSuccessCount(vo.getSuccessCount() + 1);
            } catch (Exception e) {
                EmailSendBatchVo.EmailSendResultVo result = new EmailSendBatchVo.EmailSendResultVo();
                result.setTo(to);
                result.setStatus("失败");
                result.setMessageId(null);
                results.add(result);
                vo.setFailedCount(vo.getFailedCount() + 1);
            }
        }
        
        vo.setResults(results);
        return vo;
    }
    
    @Override
    public EmailSendVo sendCustom(EmailSendCustomDto dto) {
        // Convert to Email domain
        Email email = EmailAssembler.toCustomEmailDomain(dto);
        
        // Create email record
        Email created = emailCmd.create(email);
        
        // Send email (async in real implementation)
        // Get id using reflection since Email extends BaseEntity which may not expose getId() directly
        Long emailId = null;
        try {
            java.lang.reflect.Method getIdMethod = created.getClass().getMethod("getId");
            Object id = getIdMethod.invoke(created);
            emailId = id != null ? (Long) id : null;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get email id", e);
        }
        emailCmd.send(emailId);
        
        return EmailAssembler.toSendVo(created);
    }

    // ==================== 查询 ====================
    
    @Override
    public EmailStatsVo getStats() {
        Map<String, Object> stats = emailQuery.getStatistics();
        return EmailAssembler.toStatsVo(stats);
    }
    
    @Override
    public PageResult<EmailRecordVo> listRecords(EmailRecordFindDto dto) {
        var spec = EmailAssembler.getRecordSpecification(dto);
        Page<Email> page = emailQuery.find(spec, dto.tranPage());
        return buildVoPageResult(page, EmailAssembler::toRecordVo);
    }
    
    @Override
    public PageResult<EmailTemplateVo> listTemplates(EmailTemplateFindDto dto) {
        var spec = EmailAssembler.getTemplateSpecification(dto);
        Page<EmailTemplate> page = emailTemplateQuery.find(spec, dto.tranPage());
        return buildVoPageResult(page, EmailAssembler::toTemplateVo);
    }
    
    @Override
    public EmailSmtpVo getSmtpConfig() {
        EmailSmtp smtp = emailSmtpQuery.findDefault()
            .orElseThrow(() -> new RuntimeException("SMTP配置未找到"));
        return EmailAssembler.toSmtpVo(smtp);
    }
    
    @Override
    public EmailSmtpTestVo testSmtpConnection(EmailSmtpTestDto dto) {
        EmailSmtpTestVo vo = new EmailSmtpTestVo();
        vo.setTestTime(LocalDateTime.now());
        
        try {
            // TODO: Implement actual SMTP connection test
            // This would use JavaMailSender to test connection
            vo.setConnected(true);
            vo.setMessage("SMTP连接正常");
        } catch (Exception e) {
            vo.setConnected(false);
            vo.setMessage("SMTP连接失败: " + e.getMessage());
        }
        
        return vo;
    }
    
    @Override
    public EmailTrackingVo getEmailTracking(Long id) {
        Email email = emailQuery.findAndCheck(id);
        return EmailAssembler.toTrackingVo(email);
    }
}
