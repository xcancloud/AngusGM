package cloud.xcan.angus.core.gm.interfaces.sms.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.sms.facade.internal.assembler.SmsChannelAssembler.getSmsChannelVo;
import static cloud.xcan.angus.core.gm.interfaces.sms.facade.internal.assembler.SmsChannelAssembler.getSpecification;
import static cloud.xcan.angus.core.gm.interfaces.sms.facade.internal.assembler.SmsChannelAssembler.updateDtoToDomain;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.core.gm.application.cmd.sms.SmsChannelCmd;
import cloud.xcan.angus.core.gm.application.query.sms.SmsChannelQuery;
import cloud.xcan.angus.core.gm.domain.sms.channel.SmsChannel;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.SmsChannelFacade;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.channel.SmsChannelFindDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.channel.SmsChannelUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.internal.assembler.SmsChannelAssembler;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.channel.SmsChannelVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.remote.dto.EnabledOrDisabledDto;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;


@Component
public class SmsChannelFacadeImpl implements SmsChannelFacade {

  @Resource
  private SmsChannelCmd smsChannelCmd;

  @Resource
  private SmsChannelQuery smsChannelQuery;

  @Override
  public void update(SmsChannelUpdateDto dto) {
    smsChannelCmd.update(updateDtoToDomain(dto));
  }

  @Override
  public void enabled(EnabledOrDisabledDto dto) {
    smsChannelCmd.enabled(dto.getId(), dto.getEnabled());
  }

  @Override
  public SmsChannelVo detail(Long id) {
    SmsChannel smsChannel = smsChannelQuery.detail(id);
    return getSmsChannelVo(smsChannel);
  }

  @Override
  public PageResult<SmsChannelVo> list(SmsChannelFindDto dto) {
    Page<SmsChannel> page = smsChannelQuery.list(getSpecification(dto), dto.tranPage());
    return buildVoPageResult(page, SmsChannelAssembler::toVo);
  }

}
