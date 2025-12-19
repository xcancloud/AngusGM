package cloud.xcan.angus.core.gm.interfaces.email.facade.internal.assembler;

import cloud.xcan.angus.core.gm.domain.email.Email;
import cloud.xcan.angus.core.gm.domain.email.EmailType;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailCreateDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailDetailVo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailListVo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailStatsVo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class EmailAssembler {
    
    public Email toEntity(EmailCreateDto dto) {
        Email email = new Email();
        email.setSubject(dto.getSubject());
        email.setToRecipients(dto.getToRecipients());
        email.setCcRecipients(dto.getCcRecipients());
        email.setBccRecipients(dto.getBccRecipients());
        email.setReplyTo(dto.getReplyTo());
        email.setHtmlContent(dto.getHtmlContent());
        email.setTextContent(dto.getTextContent());
        email.setTemplateId(dto.getTemplateId());
        email.setTemplateParams(dto.getTemplateParams());
        email.setAttachments(dto.getAttachments());
        if (dto.getType() != null) {
            email.setType(EmailType.valueOf(dto.getType()));
        }
        email.setPriority(dto.getPriority());
        return email;
    }
    
    public Email toEntity(EmailUpdateDto dto) {
        Email email = new Email();
        email.setSubject(dto.getSubject());
        email.setToRecipients(dto.getToRecipients());
        email.setCcRecipients(dto.getCcRecipients());
        email.setBccRecipients(dto.getBccRecipients());
        email.setHtmlContent(dto.getHtmlContent());
        email.setTextContent(dto.getTextContent());
        email.setAttachments(dto.getAttachments());
        return email;
    }
    
    public EmailDetailVo toDetailVo(Email email) {
        EmailDetailVo vo = new EmailDetailVo();
        vo.setId(email.getId());
        vo.setSubject(email.getSubject());
        vo.setToRecipients(email.getToRecipients());
        vo.setCcRecipients(email.getCcRecipients());
        vo.setBccRecipients(email.getBccRecipients());
        vo.setReplyTo(email.getReplyTo());
        vo.setHtmlContent(email.getHtmlContent());
        vo.setTextContent(email.getTextContent());
        vo.setTemplateId(email.getTemplateId());
        vo.setTemplateParams(email.getTemplateParams());
        vo.setAttachments(email.getAttachments());
        vo.setStatus(email.getStatus() != null ? email.getStatus().name() : null);
        vo.setType(email.getType() != null ? email.getType().name() : null);
        vo.setPriority(email.getPriority());
        vo.setProvider(email.getProvider());
        vo.setExternalId(email.getExternalId());
        vo.setSendTime(email.getSendTime());
        vo.setDeliverTime(email.getDeliverTime());
        vo.setErrorCode(email.getErrorCode());
        vo.setErrorMessage(email.getErrorMessage());
        vo.setRetryCount(email.getRetryCount());
        vo.setMaxRetry(email.getMaxRetry());
        vo.setCreatedAt(email.getCreatedAt());
        vo.setUpdatedAt(email.getUpdatedAt());
        return vo;
    }
    
    public EmailListVo toListVo(Email email) {
        EmailListVo vo = new EmailListVo();
        vo.setId(email.getId());
        vo.setSubject(email.getSubject());
        vo.setToRecipients(email.getToRecipients());
        vo.setStatus(email.getStatus() != null ? email.getStatus().name() : null);
        vo.setType(email.getType() != null ? email.getType().name() : null);
        vo.setPriority(email.getPriority());
        vo.setSendTime(email.getSendTime());
        vo.setCreatedAt(email.getCreatedAt());
        return vo;
    }
    
    public List<EmailListVo> toListVo(List<Email> emails) {
        return emails.stream().map(this::toListVo).collect(Collectors.toList());
    }
    
    public EmailStatsVo toStatsVo(Map<String, Object> stats) {
        EmailStatsVo vo = new EmailStatsVo();
        vo.setTotal((Long) stats.get("total"));
        vo.setByStatus((Map<String, Long>) stats.get("byStatus"));
        vo.setByType((Map<String, Long>) stats.get("byType"));
        return vo;
    }
}
