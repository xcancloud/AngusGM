package cloud.xcan.angus.core.gm.interfaces.email.facade;

import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailCreateDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailFindDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailDetailVo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailListVo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailStatsVo;

import java.util.List;

public interface EmailFacade {
    
    EmailDetailVo create(EmailCreateDto dto);
    
    EmailDetailVo update(Long id, EmailUpdateDto dto);
    
    void send(Long id);
    
    void retry(Long id);
    
    void cancel(Long id);
    
    void delete(Long id);
    
    EmailDetailVo findById(Long id);
    
    List<EmailListVo> findAll(EmailFindDto dto);
    
    EmailStatsVo getStatistics();
}
