package cloud.xcan.angus.core.gm.application.converter;

import static cloud.xcan.angus.core.gm.infra.plugin.SmsPluginStateListener.getCachedUidGenerator;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserFullName;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUsername;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isBlank;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;
import static cloud.xcan.angus.spec.utils.ObjectUtils.stringSafe;

import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.enums.MessageType;
import cloud.xcan.angus.api.gm.message.dto.MessageCenterPushDto;
import cloud.xcan.angus.api.pojo.Message;
import cloud.xcan.angus.core.gm.domain.message.center.MessageCenterOnline;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

public class MessageCenterConverter {

  public static MessageCenterOnline assembleMessageCenterOnline(String sessionId, User user,
      String userAgent, String deviceId, String remoteAddress) {
    MessageCenterOnline centerOnline = new MessageCenterOnline();
    centerOnline.setTenantId(user.getTenantId());
    centerOnline.setId(getCachedUidGenerator().getUID())
        .setUserId(user.getId())
        .setFullName(stringSafe(user.getFullName()))
        .setUserAgent(stringSafe(userAgent))
        .setDeviceId(stringSafe(deviceId))
        .setRemoteAddress(stringSafe(remoteAddress))
        .setOnline(true)
        .setOnlineDate(LocalDateTime.now())
        .setSessionId(sessionId);
    return centerOnline;
  }

  public static Message pushToNoticeDomain(MessageCenterPushDto dto) {
    return Message.newBuilder()
        .id(isBlank(dto.getMessageId()) ? UUID.randomUUID().toString() : dto.getMessageId())
        .type(MessageType.NOTICE)
        .mediaType(dto.getMediaType())
        .receiveObjectType(dto.getReceiveObjectType())
        .receiveObjectIds(dto.getReceiveObjectIds())
        .content(dto.getContent())
        .from(getUsername())
        .sendBy(nullSafe(dto.getSendBy(), getUserId()))
        .sendByName(nullSafe(dto.getSendByName(), getUserFullName()))
        .sendDate(new Date())
        .build();
  }

  //  public static Message offlineToNoticeDomain(MessageCenterOfflineDto dto) {
  //    return Message.newBuilder()
  //        .id(UUID.randomUUID().toString())
  //        .type(MessageType.KILL)
  //        .mediaType(PushMediaType.PLAIN_TEXT)
  //        .receiveObjectType(dto.getReceiveObjectType())
  //        .receiveObjectIds(dto.getReceiveObjectIds())
  //        .from(getUsername())
  //        .sendBy(getUserId())
  //        .sendByName(getUserFullName())
  //        .sendDate(new Date())
  //        .build();
  //  }

}
