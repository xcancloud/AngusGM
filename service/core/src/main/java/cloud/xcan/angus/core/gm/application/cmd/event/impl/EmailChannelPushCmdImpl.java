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

/**
 * Implementation of email channel push command for sending event notifications.
 *
 * <p>This class provides email notification functionality including:</p>
 * <ul>
 *   <li>Sending event notifications via email</li>
 *   <li>Configuring email parameters and recipients</li>
 *   <li>Handling email sending responses</li>
 *   <li>Managing email channel integration</li>
 * </ul>
 *
 * <p>The implementation converts event notifications to email format
 * and sends them using the internal email service.</p>
 */
@Slf4j
@Service("emailPushChannelCmd")
public class EmailChannelPushCmdImpl implements EventChannelPushCmd {

  @Resource
  private EmailInnerRemote emailInnerRemote;

  /**
   * Pushes event notification via email channel.
   *
   * <p>This method performs email push including:</p>
   * <ul>
   *   <li>Converting event data to email send format</li>
   *   <li>Configuring email parameters and recipients</li>
   *   <li>Sending email via internal email service</li>
   *   <li>Handling email sending responses</li>
   * </ul>
   *
   * @param eventPush Event push data containing notification details
   * @return Channel send response with success status and message
   */
  @Override
  public ChannelSendResponse push(EventPush eventPush) {
    try {
      // Create email send DTO with event data
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

      // Send email via internal email service
      emailInnerRemote.send(emailSendDto).orElseThrow();
      return new ChannelSendResponse(true, null);
    } catch (Exception e) {
      // Return failure response with error message
      return new ChannelSendResponse(false, e.getMessage());
    }
  }

  @Override
  public ReceiveChannelType getPkey() {
    return ReceiveChannelType.EMAIL;
  }
}
