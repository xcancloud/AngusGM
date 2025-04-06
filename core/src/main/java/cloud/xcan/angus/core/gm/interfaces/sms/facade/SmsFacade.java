package cloud.xcan.angus.core.gm.interfaces.sms.facade;

import cloud.xcan.angus.api.gm.sms.dto.SmsSendDto;
import cloud.xcan.angus.api.gm.sms.dto.SmsVerificationCodeCheckDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.SmsFindDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.channel.SmsChannelTestSendDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.SmsDetailVo;
import cloud.xcan.angus.remote.PageResult;
import java.util.HashSet;


public interface SmsFacade {

  void send(SmsSendDto dto);

  void channelTest(SmsChannelTestSendDto dto);

  void verificationCodeCheck(SmsVerificationCodeCheckDto dto);

  void delete(HashSet<Long> ids);

  SmsDetailVo detail(Long id);

  PageResult<SmsDetailVo> list(SmsFindDto dto);
}
