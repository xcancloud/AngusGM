package cloud.xcan.angus.core.gm.interfaces.sms.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.sms.facade.internal.assembler.SmsAssembler.dtoToDomain;

import cloud.xcan.angus.api.gm.sms.dto.SmsSendDto;
import cloud.xcan.angus.api.gm.sms.dto.SmsVerificationCodeCheckDto;
import cloud.xcan.angus.core.gm.application.cmd.sms.SmsCmd;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.SmsDoorFacade;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;


@Component
public class SmsDoorFacadeImpl implements SmsDoorFacade {

  @Resource
  private SmsCmd smsCmd;

  @Override
  public void sendSms(SmsSendDto dto) {
    smsCmd.send(dtoToDomain(dto), false);
  }

  @Override
  public void checkVerificationCode(SmsVerificationCodeCheckDto dto) {
    smsCmd.checkVerificationCode(dto.getBizKey(), dto.getMobile(), dto.getVerificationCode());
  }

}
