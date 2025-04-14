package cloud.xcan.angus.core.gm.interfaces.message.facade.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class MessageCenterOnlineVo implements Serializable {

  private Long userId;

  private String fullName;

  private String userAgent;

  private String deviceId;

  private String remoteAddress;

  private Boolean online;

  private LocalDateTime onlineDate;

  private LocalDateTime offlineDate;

}
