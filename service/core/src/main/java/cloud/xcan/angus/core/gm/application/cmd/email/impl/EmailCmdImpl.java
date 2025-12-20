package cloud.xcan.angus.core.gm.application.cmd.email.impl;

import cloud.xcan.angus.core.gm.application.cmd.email.EmailCmd;
import cloud.xcan.angus.core.gm.domain.email.Email;
import cloud.xcan.angus.core.gm.domain.email.EmailRepo;
import cloud.xcan.angus.core.gm.domain.email.EmailStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailCmdImpl implements EmailCmd {
    
    private final EmailRepo emailRepo;
    
    @Override
    @Transactional
    public Email create(Email email) {
        email.setStatus(EmailStatus.PENDING);
        email.setRetryCount(0);
        if (email.getMaxRetry() == null) {
            email.setMaxRetry(3);
        }
        return emailRepo.save(email);
    }
    
    @Override
    @Transactional
    public Email update(Long id, Email email) {
        Email existing = emailRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Email not found"));
        
        if (existing.getStatus() == EmailStatus.PENDING || existing.getStatus() == EmailStatus.FAILED) {
            if (email.getSubject() != null) existing.setSubject(email.getSubject());
            if (email.getToRecipients() != null) existing.setToRecipients(email.getToRecipients());
            if (email.getCcRecipients() != null) existing.setCcRecipients(email.getCcRecipients());
            if (email.getBccRecipients() != null) existing.setBccRecipients(email.getBccRecipients());
            if (email.getHtmlContent() != null) existing.setHtmlContent(email.getHtmlContent());
            if (email.getTextContent() != null) existing.setTextContent(email.getTextContent());
            if (email.getAttachments() != null) existing.setAttachments(email.getAttachments());
            
            return emailRepo.save(existing);
        }
        
        throw new RuntimeException("Cannot update email in current status");
    }
    
    @Override
    @Transactional
    public void send(Long id) {
        Email email = emailRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Email not found"));
        
        email.setStatus(EmailStatus.SENDING);
        email.setSendTime(LocalDateTime.now());
        emailRepo.save(email);
    }
    
    @Override
    @Transactional
    public void retry(Long id) {
        Email email = emailRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Email not found"));
        
        if (email.getStatus() == EmailStatus.FAILED) {
            if (email.getRetryCount() < email.getMaxRetry()) {
                email.setRetryCount(email.getRetryCount() + 1);
                email.setStatus(EmailStatus.PENDING);
                email.setErrorCode(null);
                email.setErrorMessage(null);
                emailRepo.save(email);
            } else {
                throw new RuntimeException("Max retry attempts reached");
            }
        } else {
            throw new RuntimeException("Email is not in failed status");
        }
    }
    
    @Override
    @Transactional
    public void cancel(Long id) {
        Email email = emailRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Email not found"));
        
        if (email.getStatus() == EmailStatus.PENDING) {
            email.setStatus(EmailStatus.CANCELLED);
            emailRepo.save(email);
        } else {
            throw new RuntimeException("Email cannot be cancelled in current status");
        }
    }
    
    @Override
    @Transactional
    public void delete(Long id) {
        emailRepo.delete(id);
    }
}
