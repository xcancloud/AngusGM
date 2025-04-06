package cloud.xcan.angus.core.gm.application.cmd.user;

import cloud.xcan.angus.api.commonlink.email.EmailBizKey;
import cloud.xcan.angus.api.commonlink.sms.SmsBizKey;
import cloud.xcan.angus.api.commonlink.user.User;


public interface UserCurrentCmd {

  void updateCurrent(User user);

  void mobileUpdate(String mobile, String country, String itc, String verificationCode,
      String linkSecret, SmsBizKey bizKey);

  void updateEmail(String email, String verificationCode, String linkSecret,
      EmailBizKey bizKey);

}
