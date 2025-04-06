package cloud.xcan.angus.core.gm.interfaces.sms.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.sms.facade.internal.assembler.SmsChannelAssembler.getSmsChannelVo;
import static cloud.xcan.angus.core.gm.interfaces.sms.facade.internal.assembler.SmsChannelAssembler.getSpecification;
import static cloud.xcan.angus.core.gm.interfaces.sms.facade.internal.assembler.SmsChannelAssembler.updateDtoToDomain;
import static java.util.Collections.singletonList;

import cloud.xcan.angus.core.gm.application.cmd.sms.SmsChannelCmd;
import cloud.xcan.angus.core.gm.application.query.sms.SmsChannelQuery;
import cloud.xcan.angus.core.gm.domain.channel.SmsChannel;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.SmsChannelFacade;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.channel.SmsChannelFindDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.channel.SmsChannelUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.internal.assembler.SmsChannelAssembler;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.channel.SmsChannelVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.remote.dto.EnabledOrDisabledDto;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
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
    smsChannelCmd.update(singletonList(updateDtoToDomain(dto)));
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
    Page<SmsChannel> page = smsChannelQuery.find(getSpecification(dto), dto.tranPage());
    return getDetailPageResult(page);
  }

  private PageResult<SmsChannelVo> getDetailPageResult(Page<SmsChannel> page) {
    if (page.hasContent()) {
      List<SmsChannelVo> vos = page.getContent()
          .stream().map(SmsChannelAssembler::toVo)
          .collect(Collectors.toList());
      return PageResult.of(page.getTotalElements(), vos);
    }
    return PageResult.empty();
  }
}
