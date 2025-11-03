package cloud.xcan.angus.api.manager.impl;

import static cloud.xcan.angus.api.manager.converter.NoticeConverter.toEmailSendDto;
import static cloud.xcan.angus.api.manager.converter.NoticeConverter.toMsgCenterPushDto;
import static cloud.xcan.angus.api.manager.converter.NoticeConverter.toSmsSendDto;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.enums.NoticeType;
import cloud.xcan.angus.api.gm.email.EmailInnerRemote;
import cloud.xcan.angus.api.gm.message.MessageCenterInnerRemote;
import cloud.xcan.angus.api.gm.notice.dto.SendNoticeDto;
import cloud.xcan.angus.api.gm.sms.SmsInnerRemote;
import cloud.xcan.angus.api.manager.NoticeManager;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NoticeManagerImpl implements NoticeManager {

  @Resource
  private SmsInnerRemote smsInnerRemote;

  @Resource
  private EmailInnerRemote emailInnerRemote;

  @Resource
  private MessageCenterInnerRemote messageCenterInnerRemote;

  @Override
  public void send(SendNoticeDto dto) {
    if (dto.getNoticeTypes().contains(NoticeType.SMS) && nonNull(smsInnerRemote)) {
      try {
        smsInnerRemote.send(toSmsSendDto(dto.getSendSmsParam())).orElseThrow();
      } catch (Exception e) {
        log.error("Sms notice exception: ", e);
      }
    }
    if (dto.getNoticeTypes().contains(NoticeType.EMAIL) && nonNull(emailInnerRemote)) {
      try {
        emailInnerRemote.send(toEmailSendDto(dto.getSendEmailParam())).orElseThrow();
      } catch (Exception e) {
        log.error("Email notice exception: ", e);
      }
    }
    if (dto.getNoticeTypes().contains(NoticeType.IN_SITE) && nonNull(messageCenterInnerRemote)) {
      try {
        messageCenterInnerRemote.send(toMsgCenterPushDto(dto.getSendInsiteParam())).orElseThrow();
      } catch (Exception e) {
        log.error("In-site notice exception: ", e);
      }
    }
  }
}
