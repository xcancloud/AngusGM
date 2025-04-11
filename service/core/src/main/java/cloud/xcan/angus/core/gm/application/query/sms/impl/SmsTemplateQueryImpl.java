package cloud.xcan.angus.core.gm.application.query.sms.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.sms.SmsTemplateQuery;
import cloud.xcan.angus.core.gm.domain.sms.template.SmsTemplate;
import cloud.xcan.angus.core.gm.domain.sms.template.SmsTemplateRepo;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

@Biz
public class SmsTemplateQueryImpl implements SmsTemplateQuery {

  @Resource
  private SmsTemplateRepo smsTemplateRepo;

  @Override
  public SmsTemplate detail(Long id) {
    return new BizTemplate<SmsTemplate>() {

      @Override
      protected SmsTemplate process() {
        return smsTemplateRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of(String.valueOf(id), "SmsTemplate"));
      }
    }.execute();
  }

  @Override
  public Page<SmsTemplate> find(Specification<SmsTemplate> spec, PageRequest pageable) {
    return new BizTemplate<Page<SmsTemplate>>() {
      @Override
      protected Page<SmsTemplate> process() {
        return smsTemplateRepo.findAll(spec, pageable);
      }
    }.execute();
  }

}
