package cloud.xcan.angus.core.gm.interfaces.email.facade;

import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailCreateDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailFindDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailDetailVo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailListVo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailStatsVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmailFacade {
    
    EmailDetailVo create(EmailCreateDto dto);
    
    EmailDetailVo update(EmailUpdateDto dto);
    
    EmailDetailVo send(EmailCreateDto dto);
    
    void retry(Long id);
    
    void cancel(Long id);
    
    void delete(Long id);
    
    EmailDetailVo findById(Long id);
    
    Page<EmailListVo> findAll(EmailFindDto dto, Pageable pageable);
    
    EmailStatsVo getStats();
}
