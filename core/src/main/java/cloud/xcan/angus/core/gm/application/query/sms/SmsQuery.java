package cloud.xcan.angus.core.gm.application.query.sms;

import cloud.xcan.angus.core.gm.domain.channel.SmsChannel;
import cloud.xcan.angus.core.gm.domain.sms.Sms;
import cloud.xcan.angus.core.gm.domain.sms.template.SmsTemplate;
import cloud.xcan.angus.extension.sms.api.SmsProvider;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;


public interface SmsQuery {

  Sms find(Long id);

  Page<Sms> find(Specification<Sms> spec, Pageable pageable);

  List<Sms> findSmsInPending(int count);

  void checkVerifyCodeSendRepeated(Sms sms);

  void checkMobileFormat(Sms sms);

  SmsChannel checkChannelEnabledAndGet();

  SmsTemplate checkTemplateAndGet(Sms sms, SmsChannel enabledSmsChannel);

  SmsProvider checkAndGetSmsProvider(SmsChannel enabledSmsChannel);

}
