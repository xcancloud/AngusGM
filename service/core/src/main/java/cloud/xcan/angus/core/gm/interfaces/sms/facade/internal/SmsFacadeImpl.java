package cloud.xcan.angus.core.gm.interfaces.sms.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.sms.facade.internal.assembler.SmsAssembler.channelTestSendDtoToDomain;
import static cloud.xcan.angus.core.gm.interfaces.sms.facade.internal.assembler.SmsAssembler.dtoToDomain;
import static cloud.xcan.angus.core.gm.interfaces.sms.facade.internal.assembler.SmsAssembler.getSpecification;
import static cloud.xcan.angus.core.gm.interfaces.sms.facade.internal.assembler.SmsAssembler.toDetailVo;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.api.gm.sms.dto.SmsSendDto;
import cloud.xcan.angus.api.gm.sms.dto.SmsVerificationCodeCheckDto;
import cloud.xcan.angus.core.gm.application.cmd.sms.SmsCmd;
import cloud.xcan.angus.core.gm.application.query.sms.SmsQuery;
import cloud.xcan.angus.core.gm.domain.sms.Sms;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.SmsFacade;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.SmsFindDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.channel.SmsChannelTestSendDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.internal.assembler.SmsAssembler;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.SmsDetailVo;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import java.util.HashSet;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;


@Component
public class SmsFacadeImpl implements SmsFacade {

  @Resource
  private SmsCmd smsCmd;

  @Resource
  private SmsQuery smsQuery;

  @Override
  public void send(SmsSendDto dto) {
    smsCmd.send(dtoToDomain(dto), false);
  }

  @Override
  public void channelTest(SmsChannelTestSendDto dto) {
    smsCmd.send(channelTestSendDtoToDomain(dto), true);
  }

  @Override
  public void verificationCodeCheck(SmsVerificationCodeCheckDto dto) {
    smsCmd.checkVerificationCode(dto.getBizKey(), dto.getMobile(), dto.getVerificationCode());
  }

  @Override
  public void delete(HashSet<Long> ids) {
    smsCmd.delete(ids);
  }

  @Override
  public SmsDetailVo detail(Long id) {
    return toDetailVo(smsQuery.detail(id));
  }

  @Override
  public PageResult<SmsDetailVo> list(SmsFindDto dto) {
    Page<Sms> page = smsQuery.list(getSpecification(dto), dto.tranPage());
    return buildVoPageResult(page, SmsAssembler::toDetailVo);
  }
}
