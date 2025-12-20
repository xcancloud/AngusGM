package cloud.xcan.angus.core.gm.interfaces.sms.facade;

import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.SmsCreateDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.SmsFindDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.SmsUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.SmsDetailVo;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.SmsListVo;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.SmsStatsVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SmsFacade {
    SmsDetailVo create(SmsCreateDto dto);
    
    SmsDetailVo update(SmsUpdateDto dto);
    
    SmsDetailVo send(SmsCreateDto dto);
    
    void retry(Long id);
    
    void cancel(Long id);
    
    void delete(Long id);
    
    SmsDetailVo findById(Long id);
    
    Page<SmsListVo> findAll(SmsFindDto dto, Pageable pageable);
    
    SmsStatsVo getStats();
}
