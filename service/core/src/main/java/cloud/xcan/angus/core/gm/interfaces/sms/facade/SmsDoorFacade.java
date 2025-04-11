package cloud.xcan.angus.core.gm.interfaces.sms.facade;

import cloud.xcan.angus.api.gm.sms.dto.SmsSendDto;
import cloud.xcan.angus.api.gm.sms.dto.SmsVerificationCodeCheckDto;


public interface SmsDoorFacade {

  void sendSms(SmsSendDto dto);

  void checkVerificationCode(SmsVerificationCodeCheckDto dto);

}
