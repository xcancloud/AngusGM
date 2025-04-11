package cloud.xcan.angus.core.gm.interfaces.sms.facade;

import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.channel.SmsChannelFindDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.channel.SmsChannelUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.channel.SmsChannelVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.remote.dto.EnabledOrDisabledDto;


public interface SmsChannelFacade {

  void update(SmsChannelUpdateDto dto);

  void enabled(EnabledOrDisabledDto dto);

  SmsChannelVo detail(Long id);

  PageResult<SmsChannelVo> list(SmsChannelFindDto dto);

}
