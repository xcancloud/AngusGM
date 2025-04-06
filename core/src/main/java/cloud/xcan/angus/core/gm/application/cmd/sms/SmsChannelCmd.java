package cloud.xcan.angus.core.gm.application.cmd.sms;

import cloud.xcan.angus.core.gm.domain.channel.SmsChannel;
import java.util.List;


public interface SmsChannelCmd {

  Void update(List<SmsChannel> channels);

  void replace(List<SmsChannel> channels);

  Void delete(List<Long> ids);

  Void enabled(Long id, Boolean enabled);

}
