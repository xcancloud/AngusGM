package cloud.xcan.angus.core.gm.application.cmd.email;

import cloud.xcan.angus.api.commonlink.email.EmailBizKey;
import cloud.xcan.angus.core.gm.domain.email.Email;
import java.util.HashSet;
import java.util.List;

public interface EmailCmd {

  void send(Email email, boolean testServer);

  void sendByJob(Email email);

  void checkVerificationCode(EmailBizKey bizKey, String email, String verificationCode);

  void delete(HashSet<Long> ids);

  void update0(List<Email> emails);
}
