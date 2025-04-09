package cloud.xcan.angus.core.gm.application.converter;

import static cloud.xcan.angus.spec.utils.DateUtils.asDate;
import static cloud.xcan.angus.spec.utils.DateUtils.formatByDateTimePattern;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.email.EmailBizKey;
import cloud.xcan.angus.api.commonlink.email.EmailType;
import cloud.xcan.angus.api.commonlink.sms.SmsBizKey;
import cloud.xcan.angus.api.enums.EventType;
import cloud.xcan.angus.api.enums.NoticeType;
import cloud.xcan.angus.api.enums.PushMediaType;
import cloud.xcan.angus.api.enums.ReceiveObjectType;
import cloud.xcan.angus.api.gm.notice.dto.SendEmailParam;
import cloud.xcan.angus.api.gm.notice.dto.SendInsiteParam;
import cloud.xcan.angus.api.gm.notice.dto.SendNoticeDto;
import cloud.xcan.angus.api.gm.notice.dto.SendSmsParam;
import cloud.xcan.angus.core.event.source.EventContent;
import cloud.xcan.angus.core.gm.domain.event.channel.EventChannel;
import cloud.xcan.angus.core.gm.domain.event.Event;
import cloud.xcan.angus.core.gm.domain.event.push.EventPush;
import cloud.xcan.angus.core.gm.interfaces.event.facade.internal.assembler.EventAssembler;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import java.util.List;
import java.util.Map;

public class EventConverter {

  public static SendNoticeDto eventToNoticeMessage(EventContent source) {
    List<NoticeType> noticeTypes = source.getNoticeTypes();
    List<Long> receiveObjectIds = source.getReceiveObjectIds();
    ReceiveObjectType receiveObjectType = source.getReceiveObjectType();
    SendNoticeDto dto = new SendNoticeDto();
    dto.setNoticeTypes(noticeTypes);
    String subject = isNotEmpty(source.getSubject()) ? source.getSubject()
        : nullSafe(source.getAppCode(), source.getServiceCode()) + " "
            + EventType.valueOf(source.getType()).getMessage();
    for (NoticeType notice : noticeTypes) {
      switch (notice) {
        case EMAIL:
          SendEmailParam sendEmailParam = new SendEmailParam();
          sendEmailParam.setBizKey(EmailBizKey.EVENT_NOTICE);
          sendEmailParam.setLanguage(PrincipalContext.getDefaultLanguage().getValue());
          sendEmailParam.setContent(source.getDescription());
          sendEmailParam.setSubject(subject);
          sendEmailParam.setVerificationCode(false);
          sendEmailParam.setHtml(true);
          sendEmailParam.setType(EmailType.TEMPLATE);
          sendEmailParam.setReceiveObjectIds(receiveObjectIds);
          sendEmailParam.setReceiveObjectType(receiveObjectType);
          dto.setSendEmailParam(sendEmailParam);
          break;
        case SMS:
          SendSmsParam sendSmsParam = new SendSmsParam();
          sendSmsParam.setBizKey(SmsBizKey.EVENT_NOTICE);
          sendSmsParam.setLanguage(PrincipalContext.getDefaultLanguage().getValue());
          sendSmsParam.setReceiveObjectIds(receiveObjectIds);
          sendSmsParam.setReceiveObjectType(receiveObjectType);
          sendSmsParam.setTemplateParams(Map.of("description", source.getDescription(),
              "date", formatByDateTimePattern(asDate(source.getTimestamp()))));
          dto.setSendSmsParam(sendSmsParam);
          break;
        case IN_SITE:
          SendInsiteParam sendInsiteParam = new SendInsiteParam();
          sendInsiteParam.setMessageId(source.getInstanceId());
          sendInsiteParam.setContent(source.getDescription());
          sendInsiteParam.setTitle(subject);
          sendInsiteParam.setPushMediaType(PushMediaType.PLAIN_TEXT);
          sendInsiteParam.setReceiveObjectIds(receiveObjectIds);
          sendInsiteParam.setReceiveObjectType(receiveObjectType);
          dto.setSendInsiteParam(sendInsiteParam);
          break;
        default: {
          // NOOP
        }
      }
    }
    return dto;
  }

  public static EventPush toPushEvent(Event event, EventChannel channel, String eventUrlPrefix) {
    return new EventPush().setEventId(event.getId())
        .setContent(EventAssembler.buildMarkDownContent(event,
            eventUrlPrefix + "/pubview/v1/event/" + event.getId(), EventAssembler.ROBOT_BR))
        .setPush(false)
        .setAddress(channel.getAddress())
        .setChannelType(channel.getType())
        .setType(event.getType())
        .setRetryTimes(0L)
        /*.setSecret(channel.getSecret())*/
        .setName(channel.getName());
  }
}
