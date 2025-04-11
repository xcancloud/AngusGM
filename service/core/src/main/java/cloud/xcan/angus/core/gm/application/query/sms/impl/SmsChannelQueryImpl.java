package cloud.xcan.angus.core.gm.application.query.sms.impl;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.sms.SmsChannelQuery;
import cloud.xcan.angus.core.gm.domain.sms.channel.SmsChannel;
import cloud.xcan.angus.core.gm.domain.sms.channel.SmsChannelRepo;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@Biz
public class SmsChannelQueryImpl implements SmsChannelQuery {

  @Resource
  private SmsChannelRepo smsChannelRepo;

  @Override
  public SmsChannel detail(Long id) {
    return new BizTemplate<SmsChannel>() {

      @Override
      protected SmsChannel process() {
        return smsChannelRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of(String.valueOf(id), "SmsChannel"));
      }
    }.execute();
  }

  @Override
  public Page<SmsChannel> find(Specification<SmsChannel> spec, Pageable pageable) {
    return new BizTemplate<Page<SmsChannel>>() {

      @Override
      protected Page<SmsChannel> process() {
        return smsChannelRepo.findAll(spec, pageable);
      }
    }.execute();
  }


  @Override
  public SmsChannel findEnabled() {
    List<SmsChannel> smsChannels = smsChannelRepo.findByEnabled(true);
    return isEmpty(smsChannels) ? null : smsChannels.get(0);
  }

}
