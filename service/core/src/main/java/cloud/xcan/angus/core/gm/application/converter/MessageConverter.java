package cloud.xcan.angus.core.gm.application.converter;

import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isUserAction;
import static cloud.xcan.angus.spec.locale.SdfLocaleHolder.getLocale;
import static cloud.xcan.angus.spec.locale.SupportedLanguage.safeLanguage;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getTenantId;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserFullName;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;
import static cloud.xcan.angus.spec.utils.ObjectUtils.stringSafe;

import cloud.xcan.angus.api.commonlink.email.EmailBizKey;
import cloud.xcan.angus.api.commonlink.email.EmailType;
import cloud.xcan.angus.api.enums.PushMediaType;
import cloud.xcan.angus.api.gm.message.dto.MessageCenterPushDto;
import cloud.xcan.angus.core.gm.domain.email.Email;
import cloud.xcan.angus.core.gm.domain.message.Message;
import cloud.xcan.angus.core.gm.domain.message.MessageSent;
import cloud.xcan.angus.core.gm.domain.message.ReceiveObject;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class MessageConverter {

  public static MessageCenterPushDto messageToPushDto(Message message) {
    return new MessageCenterPushDto()
        .setBroadcast(true)
        .setMessageId(message.getId().toString())
        .setPushMediaType(PushMediaType.PLAIN_TEXT)
        .setSendUserId(getUserId())
        .setSendUserName(getUserFullName())
        .setTitle(message.getTitle())
        .setContent(message.getContent())
        .setReceiveObjectType(message.getReceiveObjectType())
        .setReceiveObjectIds(isEmpty(message.getReceiveObjectData()) ? null :
            message.getReceiveObjectData().stream().map(ReceiveObject::getId)
                .collect(Collectors.toList()));
  }

  public static Email toSendEmailMessage(Message message) {
    return new Email()
        .setBizKey(EmailBizKey.EVENT_NOTICE)
        .setLanguage(safeLanguage(getLocale()))
        .setType(EmailType.CUSTOM)
        .setOutId(String.valueOf(message.getId()))
        .setSubject(stringSafe(message.getTitle()))
        .setReceiveObjectType(message.getReceiveObjectType())
        .setReceiveObjectIds(isEmpty(message.getReceiveObjectData()) ? null :
            message.getReceiveObjectData().stream().map(ReceiveObject::getId)
                .collect(Collectors.toList()))
        .setVerificationCode(false)
        .setVerificationCodeValidSecond(null)
        .setHtml(true)
        .setContent(message.getContent())
        .setUrgent(false)
        .setSendTenantId(isUserAction() ? getTenantId()
            : nullSafe(message.getTenantId(), -1L/* Is null when signup */))
        .setSendUserId(isUserAction() ? getUserId()
            : nullSafe(message.getCreatedBy(), -1L/* Is null when signup */));
  }

  public static MessageSent toMessageSent(Message message, Long receiveUserId, Long id) {
    return new MessageSent().setId(id)
        .setMessageId(message.getId())
        .setSentDate(LocalDateTime.now())
        .setReceiveUserId(receiveUserId)
        .setReceiveTenantId(message.getReceiveTenantId())
        .setRead(false).setDeleted(false);
  }

}
