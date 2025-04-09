package cloud.xcan.angus.core.gm.application.cmd.sms;

import cloud.xcan.angus.api.commonlink.sms.SmsBizKey;
import cloud.xcan.angus.core.gm.domain.sms.channel.SmsChannel;
import cloud.xcan.angus.core.gm.domain.sms.Sms;
import cloud.xcan.angus.extension.sms.api.SmsProvider;
import java.util.HashSet;
import java.util.List;


public interface SmsCmd {

  void send(Sms sms, boolean testChannel);

  void sendByJob(Sms sms, SmsChannel enabledChannel, SmsProvider smsProvider);

  void checkVerificationCode(SmsBizKey bizKey, String mobile, String verificationCode);

  void delete(HashSet<Long> ids);

  void update0(List<Sms> sms);
}
