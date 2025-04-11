package cloud.xcan.angus.core.gm.application.query.sms;


import cloud.xcan.angus.core.gm.domain.sms.template.SmsTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

public interface SmsTemplateQuery {

  SmsTemplate detail(Long id);

  Page<SmsTemplate> find(Specification<SmsTemplate> spec, PageRequest pageable);


}
