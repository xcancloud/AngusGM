package cloud.xcan.angus.core.gm.application.cmd.sms;

import cloud.xcan.angus.core.gm.domain.sms.channel.SmsChannel;
import java.util.List;


public interface SmsChannelCmd {

  Void update(SmsChannel channel);

  void replace(List<SmsChannel> channels);

  Void delete(List<Long> ids);

  Void enabled(Long id, Boolean enabled);

}
