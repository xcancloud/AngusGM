package cloud.xcan.angus.core.gm.application.converter;

import static cloud.xcan.angus.core.gm.infra.message.MessageCenterNoticeMessage.MESSAGE_CENTER_NOTICE;
import static cloud.xcan.angus.core.gm.infra.message.MessageCenterNoticeMessage.MESSAGE_CENTER_SIGN_OUT;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserFullName;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;
import static cloud.xcan.angus.spec.utils.ObjectUtils.stringSafe;
import static org.apache.commons.lang3.StringUtils.isBlank;

import cloud.xcan.angus.api.commonlink.mcenter.MessageCenterOnline;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.enums.PushMediaType;
import cloud.xcan.angus.api.gm.message.dto.MessageCenterPushDto;
import cloud.xcan.angus.core.gm.infra.message.MessageCenterNotice;
import cloud.xcan.angus.core.gm.infra.message.MessageCenterNoticeMessage;
import cloud.xcan.angus.core.gm.interfaces.message.facade.dto.MessageCenterOfflineDto;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

public class MessageCenterConverter {

  public static MessageCenterOnline assembleMessageCenterOnline(User user,
      String userAgent, String deviceId, String remoteAddress, boolean online) {
    MessageCenterOnline mCenterOnline = new MessageCenterOnline();
    mCenterOnline.setTenantId(user.getTenantId());
    mCenterOnline.setUserId(user.getId())
        .setFullName(stringSafe(user.getFullName()))
        .setUserAgent(stringSafe(userAgent))
        .setDeviceId(stringSafe(deviceId))
        .setRemoteAddress(stringSafe(remoteAddress))
        .setOnline(online);
    if (mCenterOnline.getOnline()) {
      mCenterOnline.setOnlineDate(LocalDateTime.now());
    } else {
      mCenterOnline.setOfflineDate(LocalDateTime.now());
    }
    return mCenterOnline;
  }

  public static MessageCenterNoticeMessage pushToNoticeDomain(MessageCenterPushDto dto) {
    return MessageCenterNoticeMessage.newBuilder()
        .id(isBlank(dto.getMessageId()) ? UUID.randomUUID().toString() : dto.getMessageId())
        .bizKey(MESSAGE_CENTER_NOTICE)
        .pushMediaType(dto.getPushMediaType())
        .receiveObjectType(dto.getReceiveObjectType())
        .content(new MessageCenterNotice().setTitle(dto.getTitle())
            .setContent(dto.getContent())
            .setSendUserId(nullSafe(dto.getSendUserId(), getUserId()))
            .setSendUserName(nullSafe(dto.getSendUserName(), getUserFullName()))
            .setSendDate(new Date())
            .setReceiveObjectIds(dto.getReceiveObjectIds())
        ).build();
  }

  public static MessageCenterNoticeMessage offlineToNoticeDomain(MessageCenterOfflineDto dto) {
    return MessageCenterNoticeMessage.newBuilder()
        .id(UUID.randomUUID().toString())
        .bizKey(MESSAGE_CENTER_SIGN_OUT)
        .pushMediaType(PushMediaType.PLAIN_TEXT)
        .receiveObjectType(dto.getReceiveObjectType())
        .content(new MessageCenterNotice().setSendUserId(getUserId())
            .setSendUserName(getUserFullName())
            .setSendDate(new Date()).setReceiveObjectIds(dto.getReceiveObjectIds()))
        .build();
  }

}
