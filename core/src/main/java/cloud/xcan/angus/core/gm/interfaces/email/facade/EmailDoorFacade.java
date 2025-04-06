package cloud.xcan.angus.core.gm.interfaces.email.facade;

import cloud.xcan.angus.api.gm.email.dto.EmailSendDto;
import cloud.xcan.angus.api.gm.email.dto.EmailVerificationCodeCheckDto;

public interface EmailDoorFacade {

  void send(EmailSendDto dto);

  void checkVerificationCode(EmailVerificationCodeCheckDto dto);

}
