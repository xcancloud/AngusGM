package cloud.xcan.angus.api.manager.impl;

import static cloud.xcan.angus.api.manager.converter.NoticeConverter.toEmailSendDto;
import static cloud.xcan.angus.api.manager.converter.NoticeConverter.toMsgCenterPushDto;
import static cloud.xcan.angus.api.manager.converter.NoticeConverter.toSmsSendDto;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.enums.NoticeType;
import cloud.xcan.angus.api.gm.email.EmailDoorRemote;
import cloud.xcan.angus.api.gm.message.MessageCenterDoorRemote;
import cloud.xcan.angus.api.gm.notice.dto.SendNoticeDto;
import cloud.xcan.angus.api.gm.sms.SmsDoorRemote;
import cloud.xcan.angus.api.manager.NoticeManager;
import cloud.xcan.angus.core.biz.Biz;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Biz
public class NoticeManagerImpl implements NoticeManager {

  @Resource
  private SmsDoorRemote smsRemote;

  @Resource
  private EmailDoorRemote emailRemote;

  @Resource
  private MessageCenterDoorRemote messageCenterDoorRemote;

  @Override
  public void send(SendNoticeDto dto) {
    if (dto.getNoticeTypes().contains(NoticeType.SMS) && nonNull(smsRemote)) {
      try {
        smsRemote.send(toSmsSendDto(dto.getSendSmsParam())).orElseThrow();
      } catch (Exception e) {
        log.error("Sms notice exception: ", e);
      }
    }
    if (dto.getNoticeTypes().contains(NoticeType.EMAIL) && nonNull(emailRemote)) {
      try {
        emailRemote.send(toEmailSendDto(dto.getSendEmailParam())).orElseThrow();
      } catch (Exception e) {
        log.error("Email notice exception: ", e);
      }
    }
    if (dto.getNoticeTypes().contains(NoticeType.IN_SITE) && nonNull(messageCenterDoorRemote)) {
      try {
        messageCenterDoorRemote.send(toMsgCenterPushDto(dto.getSendInsiteParam())).orElseThrow();
      } catch (Exception e) {
        log.error("In-site notice exception: ", e);
      }
    }
  }
}
