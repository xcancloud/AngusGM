package cloud.xcan.angus.core.gm.application.query.sms;

import cloud.xcan.angus.core.gm.domain.channel.SmsChannel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface SmsChannelQuery {

  SmsChannel detail(Long id);

  Page<SmsChannel> find(Specification<SmsChannel> spec, Pageable pageable);

  SmsChannel findEnabled();
}
