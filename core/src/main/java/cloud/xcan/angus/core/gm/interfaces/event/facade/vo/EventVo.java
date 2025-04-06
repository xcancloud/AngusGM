package cloud.xcan.angus.core.gm.interfaces.event.facade.vo;

import cloud.xcan.angus.api.enums.EventType;
import cloud.xcan.angus.core.gm.domain.event.push.EventPushStatus;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class EventVo implements Serializable {

  private Long id;

  private String code;

  private String name;

  private String description;

  private String eKey;

  private EventType type;

  private Long userId;

  private String fullname;

  private Long tenantId;

  private String tenantName;

  private String targetId;

  private String targetType;

  private String targetName;

  private String appCode;

  private String serviceCode;

  private LocalDateTime createdDate;

  private String eventViewUrl;

  private EventPushStatus pushStatus;

  private String pushMsg;

}
