package cloud.xcan.angus.core.gm.interfaces.message.facade.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MessageCenterOnlineVo implements Serializable {

  private Long userId;

  private String fullname;

  private String userAgent;

  private String deviceId;

  private String remoteAddress;

  private Boolean online;

  private LocalDateTime onlineDate;

  private LocalDateTime offlineDate;

}
