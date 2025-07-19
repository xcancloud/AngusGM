package cloud.xcan.angus.core.gm.application.query.sms;

import cloud.xcan.angus.core.gm.domain.sms.channel.SmsChannel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface SmsChannelQuery {

  SmsChannel detail(Long id);

  Page<SmsChannel> list(Specification<SmsChannel> spec, Pageable pageable);

  SmsChannel findEnabled();
}
