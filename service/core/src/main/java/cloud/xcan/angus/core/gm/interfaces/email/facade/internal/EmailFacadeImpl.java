package cloud.xcan.angus.core.gm.interfaces.email.facade.internal;

import cloud.xcan.angus.core.gm.application.cmd.email.EmailCmd;
import cloud.xcan.angus.core.gm.application.query.email.EmailQuery;
import cloud.xcan.angus.core.gm.domain.email.Email;
import cloud.xcan.angus.core.gm.domain.email.EmailStatus;
import cloud.xcan.angus.core.gm.domain.email.EmailType;
import cloud.xcan.angus.core.gm.interfaces.email.facade.EmailFacade;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailCreateDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailFindDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.internal.assembler.EmailAssembler;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailDetailVo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailListVo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailStatsVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailFacadeImpl implements EmailFacade {
    
    private final EmailCmd emailCmd;
    private final EmailQuery emailQuery;
    private final EmailAssembler emailAssembler;
    
    @Override
    public EmailDetailVo create(EmailCreateDto dto) {
        Email email = emailAssembler.toEntity(dto);
        Email created = emailCmd.create(email);
        return emailAssembler.toDetailVo(created);
    }
    
    @Override
    public EmailDetailVo update(Long id, EmailUpdateDto dto) {
        Email email = emailAssembler.toEntity(dto);
        Email updated = emailCmd.update(id, email);
        return emailAssembler.toDetailVo(updated);
    }
    
    @Override
    public void send(Long id) {
        emailCmd.send(id);
    }
    
    @Override
    public void retry(Long id) {
        emailCmd.retry(id);
    }
    
    @Override
    public void cancel(Long id) {
        emailCmd.cancel(id);
    }
    
    @Override
    public void delete(Long id) {
        emailCmd.delete(id);
    }
    
    @Override
    public EmailDetailVo findById(Long id) {
        Email email = emailQuery.findById(id)
                .orElseThrow(() -> new RuntimeException("Email not found"));
        return emailAssembler.toDetailVo(email);
    }
    
    @Override
    public List<EmailListVo> findAll(EmailFindDto dto) {
        List<Email> emails;
        
        if (dto.getStatus() != null) {
            emails = emailQuery.findByStatus(EmailStatus.valueOf(dto.getStatus()));
        } else if (dto.getType() != null) {
            emails = emailQuery.findByType(EmailType.valueOf(dto.getType()));
        } else if (dto.getRecipient() != null) {
            emails = emailQuery.findByRecipient(dto.getRecipient());
        } else if (dto.getSubject() != null) {
            emails = emailQuery.findBySubject(dto.getSubject());
        } else {
            emails = emailQuery.findAll();
        }
        
        return emailAssembler.toListVo(emails);
    }
    
    @Override
    public EmailStatsVo getStatistics() {
        Map<String, Object> stats = emailQuery.getStatistics();
        return emailAssembler.toStatsVo(stats);
    }
}
