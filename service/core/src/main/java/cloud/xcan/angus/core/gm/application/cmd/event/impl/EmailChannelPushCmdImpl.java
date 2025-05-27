package cloud.xcan.angus.core.gm.application.cmd.event.impl;

import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getDefaultLanguage;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;

import cloud.xcan.angus.api.commonlink.email.EmailBizKey;
import cloud.xcan.angus.api.commonlink.email.EmailType;
import cloud.xcan.angus.api.gm.email.EmailInnerRemote;
import cloud.xcan.angus.api.gm.email.dto.EmailSendDto;
import cloud.xcan.angus.core.gm.application.cmd.event.EventChannelPushCmd;
import cloud.xcan.angus.core.gm.domain.event.ReceiveChannelType;
import cloud.xcan.angus.core.gm.domain.event.push.EventPush;
import cloud.xcan.angus.core.gm.infra.remote.push.ChannelSendResponse;
import jakarta.annotation.Resource;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service("emailPushChannelCmd")
public class EmailChannelPushCmdImpl implements EventChannelPushCmd {

  @Resource
  private EmailInnerRemote emailInnerRemote;

  @Override
  public ChannelSendResponse push(EventPush eventPush) {
    try {
      EmailSendDto emailSendDto = new EmailSendDto();
      emailSendDto.setSubject(eventPush.getName());
      emailSendDto.setLanguage(getDefaultLanguage().getValue());
      emailSendDto.setToAddress(Set.of(eventPush.getAddress().split(",")));
      emailSendDto.setContent(eventPush.getContent());
      emailSendDto.setBizKey(EmailBizKey.EVENT_NOTICE);
      emailSendDto.setHtml(false);
      emailSendDto.setUrgent(false);
      emailSendDto.setVerificationCode(false);
      emailSendDto.setType(EmailType.CUSTOM);
      emailSendDto.setSendTenantId(getOptTenantId());
      emailSendDto.setSendUserId(getUserId());
      emailInnerRemote.send(emailSendDto).orElseThrow();
      return new ChannelSendResponse(true, null);
    } catch (Exception e) {
      return new ChannelSendResponse(false, e.getMessage());
    }
  }

  @Override
  public ReceiveChannelType getPkey() {
    return ReceiveChannelType.EMAIL;
  }
}
