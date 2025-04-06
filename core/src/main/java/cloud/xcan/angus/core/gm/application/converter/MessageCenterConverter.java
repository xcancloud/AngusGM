package cloud.xcan.angus.core.gm.application.converter;

import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserFullname;

import cloud.xcan.angus.api.commonlink.mcenter.MessageCenterOnline;
import cloud.xcan.angus.api.commonlink.user.User;
import java.time.LocalDateTime;

public class MessageCenterConverter {

  public static MessageCenterOnline assembleMessageCenterOnline(User user) {
    MessageCenterOnline mCenterOnline = new MessageCenterOnline();
    mCenterOnline.setTenantId(user.getTenantId());
    mCenterOnline.setUserId(user.getId())
        .setFullname(getUserFullname())
        // TODO MessageCenterConnectionListener#SessionConnectEvent#Object source, Message<byte[]> message, @Nullable Principal user
        // Read from Principal user
        .setUserAgent(""/*user.getUserAgent()*/) //
        .setDeviceId(""/*user.getDeviceId()*/)
        .setRemoteAddress(""/*user.getRemoteAddress()*/)
        .setOnline(true);
    if (mCenterOnline.getOnline()) {
      mCenterOnline.setOnlineDate(LocalDateTime.now());
    } else {
      mCenterOnline.setOnlineDate(LocalDateTime.now());
      mCenterOnline.setOfflineDate(LocalDateTime.now());
    }
    return mCenterOnline;
  }

}
