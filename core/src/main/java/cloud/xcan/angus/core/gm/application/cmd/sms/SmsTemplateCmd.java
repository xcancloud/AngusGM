package cloud.xcan.angus.core.gm.application.cmd.sms;

import cloud.xcan.angus.core.gm.domain.channel.SmsChannel;
import cloud.xcan.angus.core.gm.domain.sms.template.SmsTemplate;
import java.util.List;

public interface SmsTemplateCmd {

  void init(List<SmsChannel> smsChannels);

  void update(SmsTemplate smsTemplate);

}
