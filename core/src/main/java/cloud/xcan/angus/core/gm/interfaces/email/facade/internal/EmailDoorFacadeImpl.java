package cloud.xcan.angus.core.gm.interfaces.email.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.email.facade.internal.assembler.EmailAssembler.toDomain;

import cloud.xcan.angus.api.gm.email.dto.EmailSendDto;
import cloud.xcan.angus.api.gm.email.dto.EmailVerificationCodeCheckDto;
import cloud.xcan.angus.core.gm.application.cmd.email.EmailCmd;
import cloud.xcan.angus.core.gm.interfaces.email.facade.EmailDoorFacade;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class EmailDoorFacadeImpl implements EmailDoorFacade {

  @Resource
  private EmailCmd emailCmd;

  @Override
  public void send(EmailSendDto dto) {
    emailCmd.send(toDomain(dto), false);
  }

  @Override
  public void checkVerificationCode(EmailVerificationCodeCheckDto dto) {
    emailCmd.checkVerificationCode(dto.getBizKey(), dto.getEmail(), dto.getVerificationCode());
  }

}
