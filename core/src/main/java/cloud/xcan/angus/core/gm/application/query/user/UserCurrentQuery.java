package cloud.xcan.angus.core.gm.application.query.user;

import cloud.xcan.angus.api.commonlink.email.EmailBizKey;
import cloud.xcan.angus.api.commonlink.sms.SmsBizKey;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.core.gm.domain.email.Email;
import cloud.xcan.angus.core.gm.domain.sms.Sms;

public interface UserCurrentQuery {

  User currentDetail();

  void sendSms(Sms sms, String country);

  String checkSms(SmsBizKey bizKey, String mobile, String country, String verificationCode);

  void sendEmail(Email email);

  String checkEmail(EmailBizKey bizKey, String email, String verificationCode);

}
