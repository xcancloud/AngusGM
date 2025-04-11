package cloud.xcan.angus.core.gm.interfaces.message.facade.internal.assembler;

import static cloud.xcan.angus.core.gm.infra.message.MessageCenterNoticeMessage.MESSAGE_CENTER_NOTICE;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserFullname;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;
import static org.apache.commons.lang3.StringUtils.isBlank;

import cloud.xcan.angus.api.gm.message.dto.MessageCenterPushDto;
import cloud.xcan.angus.core.gm.infra.message.MessageCenterNotice;
import cloud.xcan.angus.core.gm.infra.message.MessageCenterNoticeMessage;
import cloud.xcan.angus.core.utils.GsonUtils;
import java.util.Date;
import java.util.UUID;

public class MessageCenterAssembler {

  public static MessageCenterNoticeMessage dtoToNoticeDomain(MessageCenterPushDto dto) {
    return MessageCenterNoticeMessage.newBuilder()
        .id(isBlank(dto.getMessageId()) ? UUID.randomUUID().toString() : dto.getMessageId())
        .bizKey(MESSAGE_CENTER_NOTICE)
        .pushMediaType(dto.getPushMediaType())
        .receiveObjectType(dto.getReceiveObjectType())
        .content(new MessageCenterNotice().setTitle(dto.getTitle())
            .setContent(dto.getContent())
            .setSendUserId(nullSafe(dto.getSendUserId(), getUserId()))
            .setSendUserName(nullSafe(dto.getSendUserName(), getUserFullname()))
            .setSendDate(new Date())
            .setReceiveObjectIds(dto.getReceiveObjectIds())
        ).build();
  }

  public static MessageCenterNoticeMessage messageToDomain(String message) {
    if (!message.startsWith("{")) {
      return GsonUtils.fromJson(message.substring(message.indexOf('{')),
          MessageCenterNoticeMessage.class);
    }
    return GsonUtils.fromJson(message, MessageCenterNoticeMessage.class);
  }
}
